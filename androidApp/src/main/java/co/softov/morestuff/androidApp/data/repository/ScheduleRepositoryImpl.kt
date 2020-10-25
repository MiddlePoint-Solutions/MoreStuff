package co.softov.morestuff.androidApp.data.repository


import co.softov.morestuff.androidApp.data.mapper.ScheduleDbMapper
import co.softov.morestuff.androidApp.data.mapper.ScheduleWithTitleDbMapper
import co.softov.morestuff.androidApp.data.mapper.mapList
import co.softov.morestuff.androidApp.data.utils.TimeUtils
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import co.softov.morestuff.db.SelectActiveLaterSchedulesWithTaskTitle
import co.softov.morestuff.db.SelectActiveSchedulesWithTaskTitle
import co.softov.morestuff.db.SelectActiveSchedulesWithTaskTitleByTime
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ScheduleRepositoryImpl(
    database: StuffDb,
    private val mapScheduleDb: ScheduleDbMapper,
    private val mapScheduleWithTitleDb: ScheduleWithTitleDbMapper<SelectActiveSchedulesWithTaskTitle>,
    private val mapLaterScheduleWithTitleDb: ScheduleWithTitleDbMapper<SelectActiveLaterSchedulesWithTaskTitle>,
    private val mapTimeScheduleWithTitleDb: ScheduleWithTitleDbMapper<SelectActiveSchedulesWithTaskTitleByTime>
) : ScheduleRepository {

    private val scheduleQueries = database.scheduleQueries

    override suspend fun createSchedule(
        taskId: Long,
        scheduleTime: String?
    ): Long {
        Timber.d("createTaskReminderSchedule: $taskId for $scheduleTime")
        val currentTime = TimeUtils.currentLocalDateTimeString
        val timezone = TimeUtils.currentTimeZone.id
        scheduleQueries.insertSchedule(
            task_id = taskId,
            create_time = currentTime,
            schedule_time = scheduleTime,
            timezone = timezone
        )
        return scheduleQueries.lastInsertRowId().executeAsOne()
    }

    override suspend fun getSchedule(scheduleId: Long): SimpleResult<Schedule> {
        return when (val schedule =
            scheduleQueries.selectScheduleById(scheduleId).executeAsOneOrNull()) {
            null -> Result.Failure(ScheduleDoesNotExist)
            else -> Result.Success(mapScheduleDb(schedule))
        }
    }

    override suspend fun getActiveSchedules(): SimpleResult<List<Schedule>> {
        return Result.Success(scheduleQueries.selectActiveSchedules().executeAsList()
            .map { mapScheduleDb(it) })
    }

    override suspend fun getActiveSchedulesFlow(): Flow<List<Schedule>> {
        return scheduleQueries.selectActiveSchedules()
            .asFlow()
            .mapToList()
            .map { mapList(it, mapScheduleDb) }
    }

    override suspend fun getActiveTodaySchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        val time = TimeUtils.todayTimeStringPair
        return scheduleQueries.selectActiveSchedulesWithTaskTitleByTime(time.first, time.second)
            .asFlow()
            .mapToList()
            .map {
                mapList(it, mapTimeScheduleWithTitleDb)
            }
    }

    override suspend fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        return scheduleQueries.selectActiveSchedulesWithTaskTitle()
            .asFlow()
            .mapToList()
            .map { mapList(it, mapScheduleWithTitleDb) }
    }

    override suspend fun getActiveLaterSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        return scheduleQueries.selectActiveLaterSchedulesWithTaskTitle()
            .asFlow()
            .mapToList()
            .map {
                mapList(it, mapLaterScheduleWithTitleDb)
            }
    }

    override suspend fun getActiveTomorrowSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        val time = TimeUtils.tomorrowTimeStringPair
        return scheduleQueries.selectActiveSchedulesWithTaskTitleByTime(time.first, time.second)
            .asFlow()
            .mapToList()
            .map {
                mapList(it, mapTimeScheduleWithTitleDb)
            }
    }

    override suspend fun getActiveScheduleForTask(taskId: Long): SimpleResult<Schedule> {
        return when (val schedule =
            scheduleQueries.selectActiveScheduleByTaskId(taskId).executeAsOneOrNull()) {
            null -> Result.Failure(ScheduleDoesNotExist)
            else -> Result.Success(mapScheduleDb(schedule))
        }
    }

    override suspend fun setScheduleFulfilled(scheduleId: Long): SimpleResult<Long> {
        scheduleQueries.updateScheduleActive(false, scheduleId)
        return Result.Success(scheduleId)
    }
}