package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun createSchedule(schedule: ScheduleDomain): Either<Failure, ScheduleDomain>

    suspend fun getSchedule(scheduleId: Long): Either<Failure, ScheduleDomain>

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

    suspend fun getActiveSchedulesWithTitle(): Either<Failure, List<ScheduleWithTitle>>

    fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>

    suspend fun getActiveSchedulesWithTitleByTime(
        startTime: String,
        endTime: String
    ): Either<Failure, List<ScheduleWithTitle>>

    fun getActiveSchedulesWithTitleByTimeFlow(
        startTime: String,
        endTime: String
    ): Flow<List<ScheduleWithTitle>>

    suspend fun getActiveScheduleWithTitle(scheduleId: Long): Either<Failure, ScheduleWithTitle>

    suspend fun getTodayActiveSchedulesWithTitle(): List<ScheduleWithTitle>

    suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure, Long>

    suspend fun getActiveSchedulesForTask(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>>

    fun getActiveSchedulesForTaskFlow(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Flow<List<ScheduleDomain>>

    suspend fun getActiveSchedulesWithStaleReminders(): List<ScheduleWithTitle>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int>

}

object ScheduleDoesNotExist : FeatureFailure
