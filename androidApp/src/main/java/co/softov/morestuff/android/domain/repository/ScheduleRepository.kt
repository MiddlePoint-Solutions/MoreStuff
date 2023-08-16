package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
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

    suspend fun getActiveSchedulesForTask(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>>

    fun getActiveSchedulesForTaskFlow(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int>

}

object ScheduleDoesNotExist : FeatureFailure
