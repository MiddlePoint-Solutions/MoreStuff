package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun createSchedule(schedule: Schedule): Either<Failure, Schedule>

    suspend fun getSchedule(scheduleId: Long): Either<Failure, Schedule>

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

    suspend fun getActiveScheduleForTask(taskId: Long): Either<Failure, Schedule>

    fun getActiveScheduleForTaskFlow(taskId: Long): Flow<Either<Failure, Schedule>>

    suspend fun getActiveSchedulesWithStaleReminders(): List<ScheduleWithTitle>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure, Int>

    // TODO: remove these:

    fun getTodayActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>

    fun getTomorrowActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>

    fun getLaterActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>

}

object ScheduleDoesNotExist : FeatureFailure
