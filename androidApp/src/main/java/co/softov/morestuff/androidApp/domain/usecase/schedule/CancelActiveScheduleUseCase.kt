package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.Scheduler
import co.softov.morestuff.androidApp.domain.model.Result

interface CancelActiveScheduleUseCase {
    suspend operator fun invoke(taskId: Long)
}

class CancelActiveScheduleUseCaseImpl(
    private val getActiveSchedule: GetActiveSchedule,
    private val setScheduleFulfilled: SetScheduleFulfilled,
    private val scheduler: Scheduler
) : CancelActiveScheduleUseCase {

    override suspend fun invoke(taskId: Long) {
        when (val result = getActiveSchedule(taskId)) {
            is Result.Success -> {
                val scheduleId = result.value.id
                scheduler.cancelSchedule(scheduleId)
                setScheduleFulfilled(scheduleId)
            }
        }
    }
}



