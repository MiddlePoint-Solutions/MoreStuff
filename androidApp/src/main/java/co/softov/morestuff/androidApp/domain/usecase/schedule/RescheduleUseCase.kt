package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult

interface RescheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean>
}

class RescheduleUseCaseImpl(
    private val cancelActiveSchedule: CancelActiveScheduleForTask,
    private val createSchedule: CreateScheduleUseCase
) : RescheduleUseCase {

    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Boolean> {
        cancelActiveSchedule(taskId)
        createSchedule(taskId, priority)
        return Result.Success(true)
    }

} 



