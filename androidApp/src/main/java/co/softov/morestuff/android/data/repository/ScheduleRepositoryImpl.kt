package co.softov.morestuff.android.data.repository


import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import co.softov.morestuff.android.data.mapper.ScheduleDbMapper
import co.softov.morestuff.android.data.mapper.ScheduleWithTitleDbMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
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
    ): Either<Failure, Schedule> {
        Timber.d("createTaskReminderSchedule: $taskId for $scheduleTime")
        val scheduleId: Long = scheduleQueries.transactionWithResult {
            val currentTime = TimeUtils.nowLocalDateTimeString
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

    override suspend fun getSchedule(scheduleId: Long): Either<Failure, Schedule> {
        return when (val schedule =
            scheduleQueries.selectScheduleById(scheduleId).executeAsOneOrNull()) {
            null -> Left(ScheduleDoesNotExist)
            else -> Right(mapScheduleDb(schedule))
        }
    }

    override suspend fun getActiveSchedules(): Either<Failure, List<Schedule>> {
        return Right(scheduleQueries.selectActiveSchedules().executeAsList()
            .map { mapScheduleDb(it) })
    }

    override suspend fun getActiveSchedulesWithTitle(): List<ScheduleWithTitle> {
        return scheduleQueries.selectActiveSchedulesWithTaskTitle(mapper = mapScheduleWithTitleDb)
            .executeAsList()
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

    override suspend fun getActiveScheduleWithTitle(scheduleId: Long): Either<Failure, ScheduleWithTitle> {
        return scheduleQueries
            .selectActiveScheduleWithTaskTitle(scheduleId, mapper = mapScheduleWithTitleDb)
            .executeAsOneOrNull()
            ?.let { Right(it) }
            ?: Left(ScheduleDoesNotExist)
    }

    override fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
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

    override suspend fun getActiveScheduleForTask(taskId: Long): Either<Failure, Schedule> {
        return when (val schedule =
            scheduleQueries.selectActiveScheduleByTaskId(taskId).executeAsOneOrNull()) {
            null -> Left(ScheduleDoesNotExist)
            else -> Right(mapScheduleDb(schedule))
        }
    }

    override suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure, Long> {
        scheduleQueries.updateScheduleActive(false, scheduleId)
        return Right(scheduleId)
    }

    override suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int> {
        val limit =
            scheduleQueries.countTaskSchedulesByTime(taskId, startTime, endTime).executeAsOne()
        return Right(limit.toInt())
    }
}