package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.PreferenceRepository
import java.util.Calendar

interface RescheduleTask {
    suspend operator fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean>
}

class RescheduleTaskImpl(
    private val preferenceRepository: PreferenceRepository,
    private val cancelActiveSchedule: CancelActiveScheduleForTask,
    private val createSchedule: CreateSchedule
) : RescheduleTask {

    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean> {
        cancelActiveSchedule(taskId)
        createSchedule(taskId, getScheduleTime(priority))
        return Result.Success(true)
    }

    private suspend fun getScheduleTime(priority: Priority): Long {
        return when (priority) {
            is Priority.Later -> 0L
            is Priority.Today -> Calendar.getInstance().timeInMillis + preferenceRepository.getTodayReminderDelay()
            is Priority.Tomorrow ->
                Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 11)
                }.timeInMillis
        }
    }
} 



