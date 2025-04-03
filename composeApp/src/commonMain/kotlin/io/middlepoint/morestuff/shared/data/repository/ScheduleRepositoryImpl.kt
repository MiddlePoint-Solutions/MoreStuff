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
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
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
        schedule: ScheduleDomain,
    ): Either<Failure, ScheduleDomain> = scheduleQueries.transactionWithResult {
        val data = mapScheduleDomain(schedule)
        scheduleQueries.insertSchedule(data)
        schedule.copy(id = lastInsertId).right()
    }

    override suspend fun getSchedule(scheduleId: Long): Either<Failure, ScheduleDomain> =
        scheduleQueries.selectScheduleById(scheduleId, mapper.scheduleDbMapper)
            .executeAsOneOrNull()
            ?.right() ?: ScheduleDoesNotExist.left()

    override suspend fun getSchedules(scheduleIds: List<Long>): Either<Failure, List<ScheduleDomain>> =
        scheduleQueries.selectSchedulesById(scheduleIds, mapper.scheduleDbMapper)
            .executeAsList()
            .right()

    override suspend fun getActiveSchedules(): Either<Failure, List<ScheduleDomain>> {
        val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
        return scheduleQueries
            .selectActiveSchedules(allScheduleTypes, mapper.scheduleDbMapper)
            .executeAsList()
            .right()
    }

    override fun getActiveSchedulesFlow(): Flow<List<ScheduleDomain>> {
        val allScheduleTypes = listOf(ScheduleType.OneTime, ScheduleType.Reminder)
        return scheduleQueries.selectActiveSchedules(allScheduleTypes, mapper.scheduleDbMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override suspend fun getActiveSchedulesByTime(
        startTime: String,
        endTime: String,
    ): Either<Failure, List<ScheduleDomain>> =
        getActiveSchedulesByTimeFlow(startTime, endTime).firstOrNull()
            ?.right() ?: ScheduleDoesNotExist.left()

    override fun getActiveSchedulesByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<ScheduleDomain>> = scheduleQueries
        .selectActiveSchedulesFromStartToEndTime(startTime, endTime, mapper.scheduleDbMapper)
        .asFlow()
        .mapToList(Dispatchers.IO)

    override suspend fun getActiveSchedulesForTasks(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> =
        scheduleQueries.selectActiveScheduleByTaskId(
            taskIds,
            scheduleType,
            mapper = mapper.scheduleDbMapper
        ).executeAsList().right()

    override fun getActiveSchedulesForTaskFlow(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>> =
        scheduleQueries.selectActiveScheduleByTaskId(
            taskIds,
            scheduleType,
            mapper = mapper.scheduleDbMapper
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

    override suspend fun restoreSchedule(
        schedule: ScheduleDomain,
        newTaskId: Long
    ): Either<Failure, ScheduleDomain> = scheduleQueries.transactionWithResult {
        val scheduleToRestore = schedule.copy(
            id = 0L,
            taskId = newTaskId,
            active = true
        )

        val data = mapScheduleDomain(scheduleToRestore)
        scheduleQueries.insertSchedule(data)
        scheduleToRestore.copy(id = lastInsertId).right()
    }

}