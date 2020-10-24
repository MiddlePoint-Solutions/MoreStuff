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
    private val cancelActiveSchedule: CancelActiveScheduleForTask,
    private val createSchedule: CreateSchedule
) : RescheduleTask {

    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean> {
        cancelActiveSchedule(taskId)
        createSchedule(taskId, priority)
        return Result.Success(true)
    }

} 



