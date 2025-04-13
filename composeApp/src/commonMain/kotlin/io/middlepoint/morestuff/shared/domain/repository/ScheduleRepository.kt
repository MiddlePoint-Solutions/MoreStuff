package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun createSchedule(schedule: Schedule): Either<Failure, Schedule>

    suspend fun getSchedule(scheduleId: Long): Either<Failure, Schedule>
    suspend fun getSchedules(scheduleIds: List<Long>): Either<Failure, List<Schedule>>

    suspend fun getActiveSchedules(): Either<Failure, List<Schedule>>

    fun getActiveSchedulesFlow(): Flow<List<Schedule>>

    suspend fun getActiveSchedulesByTime(
        startTime: String,
        endTime: String
    ): Either<Failure, List<Schedule>>

    fun getActiveSchedulesByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<Schedule>>

    suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure, Long>

    suspend fun getActiveSchedulesForTasks(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<Schedule>>

    fun getActiveSchedulesForTaskFlow(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Flow<List<Schedule>>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int>

}

object ScheduleDoesNotExist : FeatureFailure
