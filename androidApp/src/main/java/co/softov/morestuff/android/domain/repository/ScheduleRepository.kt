package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun createSchedule(taskId: Long, scheduleTime: String?): Either<Failure, Schedule>
    suspend fun getSchedule(scheduleId: Long): Either<Failure, Schedule>

    suspend fun getActiveSchedules(
        startTime: String? = null,
        endTime: String? = null
    ): Either<Failure, List<Schedule>>

    suspend fun getActiveSchedulesWithTitle(): List<ScheduleWithTitle>
    suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure, Long>
    suspend fun getActiveScheduleForTask(taskId: Long): Either<Failure, Schedule>
    suspend fun getActiveScheduleWithTitle(scheduleId: Long): Either<Failure, ScheduleWithTitle>

    suspend fun getActiveSchedulesFlow(): Flow<List<Schedule>>
    fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveTodaySchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveLaterSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveTomorrowSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int>
}

object ScheduleDoesNotExist : FeatureFailure
