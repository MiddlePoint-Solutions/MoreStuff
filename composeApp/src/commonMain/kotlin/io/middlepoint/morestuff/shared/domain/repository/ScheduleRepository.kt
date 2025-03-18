package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun createSchedule(schedule: ScheduleDomain): Either<Failure, ScheduleDomain>

    suspend fun getSchedule(scheduleId: Long): Either<Failure, ScheduleDomain>
    suspend fun getSchedules(scheduleIds: List<Long>): Either<Failure, List<ScheduleDomain>>

    suspend fun getActiveSchedules(): Either<Failure, List<ScheduleDomain>>

    fun getActiveSchedulesFlow(): Flow<List<ScheduleDomain>>

    suspend fun getActiveSchedulesByTime(
        startTime: String,
        endTime: String
    ): Either<Failure, List<ScheduleDomain>>

    fun getActiveSchedulesByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<ScheduleDomain>>

    suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure, Long>

    suspend fun getActiveSchedulesForTasks(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>>

    fun getActiveSchedulesForTaskFlow(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int>

}

object ScheduleDoesNotExist : FeatureFailure
