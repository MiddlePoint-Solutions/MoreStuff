package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.service.Scheduler

interface CancelActiveScheduleUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure,Schedule>
}

class CancelActiveScheduleUseCaseImpl(
    private val scheduler: Scheduler,
    private val getActiveSchedule: GetActiveScheduleUseCase,
    private val setScheduleFulfilled: SetScheduleFulfilledUseCase
) : CancelActiveScheduleUseCase {

    override suspend fun invoke(taskId: Long): Either<Failure,Schedule> {
        return getActiveSchedule(taskId).also { result ->
            if (result is Either.Right) {
                val scheduleId = result.value.id
                setScheduleFulfilled(scheduleId)
                scheduler.cancelSchedule(scheduleId)
            }
        }
    }
}



