package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.ScheduleWithTitle

interface ScheduleRepository {

    suspend fun createSchedule(taskId: Long, scheduleTime: String?): Either<Failure, Schedule>
    suspend fun getSchedule(scheduleId: Long): Either<Failure,Schedule>
    suspend fun getActiveSchedules(): Either<Failure,List<Schedule>>
    suspend fun setScheduleFulfilled(scheduleId: Long): Either<Failure,Long>
    suspend fun getActiveSchedulesFlow(): Flow<List<Schedule>>
    suspend fun getActiveScheduleForTask(taskId: Long): Either<Failure,Schedule>

    suspend fun getActiveScheduleWithTitle(scheduleId: Long): Either<Failure,ScheduleWithTitle>
    suspend fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveTodaySchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveLaterSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveTomorrowSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>

    suspend fun countTodayTaskSchedules(
        taskId: Long,
        startTime: String,
        endTime: String
    ): Either<Failure,Int>
}

object ScheduleDoesNotExist : FeatureFailure
