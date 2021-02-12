package co.softov.morestuff.android.data.repository


import co.softov.morestuff.android.data.mapper.ScheduleDbMapper
import co.softov.morestuff.android.data.mapper.ScheduleWithTitleDbMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Result
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ScheduleRepositoryImpl(
    database: StuffDb,
    private val mapScheduleDb: ScheduleDbMapper,
    private val mapScheduleWithTitleDb: ScheduleWithTitleDbMapper
) : ScheduleRepository {

    private val scheduleQueries = database.scheduleQueries
    private val lastInsertId: Long get() = scheduleQueries.lastInsertRowId().executeAsOne()

    override suspend fun createSchedule(
        taskId: Long,
        scheduleTime: String?
    ): SimpleResult<Schedule> {
        Timber.d("createTaskReminderSchedule: $taskId for $scheduleTime")
        val scheduleId: Long = scheduleQueries.transactionWithResult {
            val currentTime = TimeUtils.currentLocalDateTimeString
            val timezone = TimeUtils.currentTimeZone.id
            scheduleQueries.insertSchedule(
                task_id = taskId,
                create_time = currentTime,
                schedule_time = scheduleTime,
                timezone = timezone
            )
            lastInsertId
        }
        return getSchedule(scheduleId)
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
        return scheduleQueries.selectActiveSchedulesWithTaskTitleByTime(
            time.first,
            time.second,
            mapper = mapScheduleWithTitleDb
        )
            .asFlow()
            .mapToList()
    }

    override suspend fun getActiveScheduleWithTitle(scheduleId: Long): SimpleResult<ScheduleWithTitle> {
        return scheduleQueries
            .selectActiveScheduleWithTaskTitle(scheduleId, mapper = mapScheduleWithTitleDb)
            .executeAsOneOrNull()
            ?.let { Result.Success(it) }
            ?: Result.Failure(ScheduleDoesNotExist)
    }

    override suspend fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        return scheduleQueries.selectActiveSchedulesWithTaskTitle(mapper = mapScheduleWithTitleDb)
            .asFlow()
            .mapToList()
    }

    override suspend fun getActiveLaterSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        return scheduleQueries.selectActiveLaterSchedulesWithTaskTitle(mapper = mapScheduleWithTitleDb)
            .asFlow()
            .mapToList()
    }

    override suspend fun getActiveTomorrowSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        val time = TimeUtils.tomorrowTimeStringPair
        return scheduleQueries.selectActiveSchedulesWithTaskTitleByTime(
            time.first,
            time.second,
            mapper = mapScheduleWithTitleDb
        )
            .asFlow()
            .mapToList()
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

    override suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): SimpleResult<Int> {
        val limit =
            scheduleQueries.countTaskSchedulesByTime(taskId, startTime, endTime).executeAsOne()
        return Result.Success(limit.toInt())
    }
}