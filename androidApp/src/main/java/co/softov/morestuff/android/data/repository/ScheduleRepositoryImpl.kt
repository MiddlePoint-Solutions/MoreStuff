package co.softov.morestuff.android.data.repository


import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right
import arrow.core.rightIfNotNull
import co.softov.morestuff.android.data.mapper.ScheduleDataMapper
import co.softov.morestuff.android.data.mapper.ScheduleDbMapper
import co.softov.morestuff.android.data.mapper.ScheduleDomainMapper
import co.softov.morestuff.android.data.mapper.ScheduleWithTitleDbMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleDoesNotExist
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl(
    database: StuffDb,
    private val mapScheduleData: ScheduleDataMapper,
    private val mapScheduleDomain: ScheduleDomainMapper,
    private val mapScheduleDb: ScheduleDbMapper,
    private val mapScheduleWithTitleDb: ScheduleWithTitleDbMapper,
    private val timeManager: TimeManager,
) : ScheduleRepository {

    private val scheduleQueries = database.scheduleQueries
    private val lastInsertId: Long get() = scheduleQueries.lastInsertRowId().executeAsOne()

    override suspend fun createSchedule(
        schedule: ScheduleDomain,
    ): Either<Failure, ScheduleDomain> {
        val scheduleId = scheduleQueries.transactionWithResult {
            val data = mapScheduleDomain(schedule)
            scheduleQueries.insertSchedule(data)
            lastInsertId
        }
        return schedule.copy(id = scheduleId).right()
    }

    override suspend fun getSchedule(scheduleId: Long): Either<Failure, ScheduleDomain> {
        return when (val schedule =
            scheduleQueries.selectScheduleById(scheduleId).executeAsOneOrNull()) {
            null -> Left(ScheduleDoesNotExist)
            else -> Right(mapScheduleData(schedule))
        }
    }

    override suspend fun getActiveSchedules(): Either<Failure, List<ScheduleDomain>> {
        return scheduleQueries
            .selectActiveSchedules()
            .executeAsList()
            .map(mapScheduleData)
            .right()
    }

    override fun getActiveSchedulesFlow(): Flow<List<ScheduleDomain>> {
        return scheduleQueries.selectActiveSchedules()
            .asFlow()
            .mapToList()
            .map { mapList(it, mapScheduleData) }
    }

    override suspend fun getActiveSchedulesByTime(
        startTime: String,
        endTime: String,
    ): Either<Failure, List<ScheduleDomain>> = scheduleQueries
        .selectActiveSchedulesFromStartToEndTime(startTime, endTime)
        .executeAsList()
        .map(mapScheduleData)
        .right()

    override fun getActiveSchedulesByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<ScheduleDomain>> {
        return scheduleQueries.selectActiveSchedulesFromStartToEndTime(startTime, endTime)
            .asFlow()
            .mapToList()
            .map { mapList(it, mapScheduleData) }
    }

    override suspend fun getActiveSchedulesWithTitle(): Either<Failure, List<ScheduleWithTitle>> {
        return scheduleQueries
            .selectActiveSchedulesWithTaskTitle(mapScheduleWithTitleDb)
            .executeAsList()
            .right()
    }

    override suspend fun getTodayActiveSchedulesWithTitle(): List<ScheduleWithTitle> {
        val time = timeManager.todayTimeStringPair
        return scheduleQueries.selectActiveSchedulesWithTaskTitleByTime(
            time.first,
            time.second,
            mapper = mapScheduleWithTitleDb
        ).executeAsList()
    }

    override suspend fun getActiveScheduleWithTitle(scheduleId: Long): Either<Failure, ScheduleWithTitle> {
        return scheduleQueries
            .selectActiveScheduleWithTaskTitle(scheduleId, mapper = mapScheduleWithTitleDb)
            .executeAsOneOrNull()
            ?.let { Right(it) }
            ?: Left(ScheduleDoesNotExist)
    }

    override fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>> {
        return scheduleQueries.selectActiveSchedulesWithTaskTitle(
            mapper = mapScheduleWithTitleDb
        )
            .asFlow()
            .mapToList()
    }

    override suspend fun getActiveSchedulesWithTitleByTime(
        startTime: String,
        endTime: String
    ): Either<Failure, List<ScheduleWithTitle>> {
        return scheduleQueries
            .selectActiveSchedulesWithTaskTitleByTime(
                startTime,
                endTime,
                mapScheduleWithTitleDb
            ).executeAsList()
            .right()
    }

    override fun getActiveSchedulesWithTitleByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<ScheduleWithTitle>> {
        return scheduleQueries
            .selectActiveSchedulesWithTaskTitleByTime(
                startTime,
                endTime,
                mapScheduleWithTitleDb
            ).asFlow()
            .mapToList()
    }

    override suspend fun getActiveSchedulesForTask(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> =
        getActiveSchedulesForTaskFlow(taskId, scheduleType).firstOrNull()
            .rightIfNotNull { ScheduleDoesNotExist }

    override fun getActiveSchedulesForTaskFlow(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>> =
        scheduleQueries.selectActiveScheduleByTaskId(taskId, scheduleType, mapper = mapScheduleDb)
            .asFlow()
            .mapToList()


    override suspend fun getActiveSchedulesWithStaleReminders(): List<ScheduleWithTitle> {
        val time = timeManager.todayTimeStringPair
        return scheduleQueries.selectActiveSchedulesWithStaleReminders(
            end = time.second,
            mapper = mapScheduleWithTitleDb
        ).executeAsList()
    }

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