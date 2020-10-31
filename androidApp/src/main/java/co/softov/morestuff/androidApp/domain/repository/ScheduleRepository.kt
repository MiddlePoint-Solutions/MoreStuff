package co.softov.morestuff.androidApp.domain.repository


import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.androidApp.domain.Failure
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle

interface ScheduleRepository {

    suspend fun createSchedule(taskId: Long, scheduleTime: String?): SimpleResult<Schedule>
    suspend fun getSchedule(scheduleId: Long): SimpleResult<Schedule>
    suspend fun getActiveSchedules(): SimpleResult<List<Schedule>>
    suspend fun setScheduleFulfilled(scheduleId: Long) : SimpleResult<Long>
    suspend fun getActiveSchedulesFlow() : Flow<List<Schedule>>
    suspend fun getActiveScheduleForTask(taskId: Long): SimpleResult<Schedule>

    suspend fun getActiveScheduleWithTitle(scheduleId: Long): SimpleResult<ScheduleWithTitle>
    suspend fun getActiveSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveTodaySchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveLaterSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
    suspend fun getActiveTomorrowSchedulesWithTitleFlow(): Flow<List<ScheduleWithTitle>>
}

object ScheduleDoesNotExist: Failure.FeatureFailure()