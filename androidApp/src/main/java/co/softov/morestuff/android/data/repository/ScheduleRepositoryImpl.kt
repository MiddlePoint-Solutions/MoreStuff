package co.softov.morestuff.android.data.repository


import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.right
import arrow.core.rightIfNotNull
import co.softov.morestuff.android.data.mapper.ScheduleDbMapper
import co.softov.morestuff.android.data.mapper.ScheduleDomainMapper
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ScheduleRepositoryImpl(
    database: StuffDb,
    private val mapScheduleDomain: ScheduleDomainMapper,
    private val mapScheduleDb: ScheduleDbMapper,
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
        scheduleQueries.selectScheduleById(scheduleId, mapScheduleDb)
            .executeAsOneOrNull()
            .rightIfNotNull { ScheduleDoesNotExist }


    override suspend fun getActiveSchedules(): Either<Failure, List<ScheduleDomain>> {
        return scheduleQueries
            .selectActiveSchedules(mapScheduleDb)
            .executeAsList()
            .right()
    }

    override fun getActiveSchedulesFlow(): Flow<List<ScheduleDomain>> {
        return scheduleQueries.selectActiveSchedules(mapScheduleDb)
            .asFlow()
            .mapToList()
    }

    override suspend fun getActiveSchedulesByTime(
        startTime: String,
        endTime: String,
    ): Either<Failure, List<ScheduleDomain>> =
        getActiveSchedulesByTimeFlow(startTime, endTime).firstOrNull()
            .rightIfNotNull { ScheduleDoesNotExist }

    override fun getActiveSchedulesByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<ScheduleDomain>> = scheduleQueries
        .selectActiveSchedulesFromStartToEndTime(startTime, endTime, mapScheduleDb)
        .asFlow()
        .mapToList()

    override suspend fun getActiveSchedulesForTask(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> = getActiveSchedulesForTaskFlow(taskId, scheduleType)
        .firstOrNull()
        .rightIfNotNull { ScheduleDoesNotExist }

    override fun getActiveSchedulesForTaskFlow(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>> =
        scheduleQueries.selectActiveScheduleByTaskId(taskId, scheduleType, mapper = mapScheduleDb)
            .asFlow()
            .mapToList()

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