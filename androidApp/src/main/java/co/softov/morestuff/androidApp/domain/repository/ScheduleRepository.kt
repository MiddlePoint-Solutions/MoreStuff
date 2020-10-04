package co.softov.morestuff.androidApp.domain.repository


import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.androidApp.domain.Failure
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle

interface ScheduleRepository {

    suspend fun createSchedule(taskId: Long, scheduleTime: Long): SimpleResult<Long>
    suspend fun getSchedule(scheduleId: Long): SimpleResult<Schedule>
    suspend fun getActiveSchedules(): SimpleResult<List<Schedule>>
    suspend fun setScheduleFulfilled(scheduleId: Long) : SimpleResult<Long>
    suspend fun getActiveSchedulesFlow() : SimpleResult<Flow<List<Schedule>>>
    suspend fun getActiveScheduleForTask(taskId: Long): SimpleResult<Schedule>
    suspend fun getActiveSchedulesWithTitleFlow(): SimpleResult<Flow<List<ScheduleWithTitle>>>
}

object ScheduleDoesNotExist: Failure.FeatureFailure()