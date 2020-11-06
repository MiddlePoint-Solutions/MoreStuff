package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult

interface CancelActiveScheduleUseCase {
    suspend operator fun invoke(taskId: Long): SimpleResult<Schedule>
}

class CancelActiveScheduleUseCaseImpl(
    private val getActiveSchedule: GetActiveSchedule,
    private val setScheduleFulfilled: SetScheduleFulfilledUseCase
) : CancelActiveScheduleUseCase {

    override suspend fun invoke(taskId: Long): SimpleResult<Schedule> {
        return getActiveSchedule(taskId).also { result ->
            if (result is Result.Success) {
                val scheduleId = result.value.id
                setScheduleFulfilled(scheduleId)
            }
        }
    }
}



