package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult

interface RescheduleUseCase {
    suspend operator fun invoke(taskId: Long, priority: Priority): SimpleResult<Schedule>
}

class RescheduleUseCaseImpl(
    private val cancelActiveSchedule: CancelActiveScheduleUseCase,
    private val createSchedule: CreateScheduleUseCase
) : RescheduleUseCase {

    override suspend fun invoke(taskId: Long, priority: Priority): SimpleResult<Schedule> {
        cancelActiveSchedule(taskId)
        return createSchedule(taskId, priority)
    }

} 



