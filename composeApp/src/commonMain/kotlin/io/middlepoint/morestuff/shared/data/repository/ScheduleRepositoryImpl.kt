package io.middlepoint.morestuff.shared.data.repository


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.mapScheduleDomain
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.repository.ScheduleDoesNotExist
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ScheduleRepositoryImpl(
    database: StuffDb,
    private val mapper: DataMappers,
) : ScheduleRepository {

    private val scheduleQueries = database.scheduleQueries
    private val lastInsertId: Long get() = scheduleQueries.lastInsertRowId().executeAsOne()

    override suspend fun createSchedule(
      schedule: Schedule,
    ): Either<Failure, Schedule> = scheduleQueries.transactionWithResult {
        val data = mapScheduleDomain(schedule)
        scheduleQueries.insertSchedule(data)
        schedule.copy(id = lastInsertId).right()
    }

    override suspend fun getSchedule(scheduleId: Long): Either<Failure, Schedule> =
        scheduleQueries.selectScheduleById(scheduleId, mapper.scheduleDataMapper)
            .executeAsOneOrNull()
            ?.right() ?: ScheduleDoesNotExist.left()

    override suspend fun getSchedules(scheduleIds: List<Long>): Either<Failure, List<Schedule>> =
        scheduleQueries.selectSchedulesById(scheduleIds, mapper.scheduleDataMapper)
            .executeAsList()
            .right()

    override suspend fun getActiveSchedules(): Either<Failure, List<Schedule>> {
        val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
        return scheduleQueries
            .selectActiveSchedules(allScheduleTypes, mapper.scheduleDataMapper)
            .executeAsList()
            .right()
    }

    override fun getActiveSchedulesFlow(): Flow<List<Schedule>> {
        val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
        return scheduleQueries.selectActiveSchedules(allScheduleTypes, mapper.scheduleDataMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override suspend fun getActiveSchedulesByTime(
        startTime: String,
        endTime: String,
    ): Either<Failure, List<Schedule>> =
        getActiveSchedulesByTimeFlow(startTime, endTime).firstOrNull()
            ?.right() ?: ScheduleDoesNotExist.left()

    override fun getActiveSchedulesByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<Schedule>> = scheduleQueries
        .selectActiveSchedulesFromStartToEndTime(startTime, endTime, mapper.scheduleDataMapper)
        .asFlow()
        .mapToList(Dispatchers.IO)

    override suspend fun getActiveSchedulesForTasks(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<Schedule>> =
        scheduleQueries.selectActiveScheduleByTaskId(
            taskIds,
            scheduleType,
            mapper = mapper.scheduleDataMapper
        ).executeAsList().right()

    override fun getActiveSchedulesForTaskFlow(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Flow<List<Schedule>> =
        scheduleQueries.selectActiveScheduleByTaskId(
            taskIds,
            scheduleType,
            mapper = mapper.scheduleDataMapper
        )
            .asFlow()
            .mapToList(Dispatchers.IO)

    override suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure, Long> {
        scheduleQueries.updateScheduleActive(false, scheduleId)
        return Right(scheduleId)
    }

    override suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String,
    ): Either<Failure, Int> {
        val limit =
            scheduleQueries.countTaskSchedulesByTime(taskId, startTime, endTime).executeAsOne()
        return Right(limit.toInt())
    }

}