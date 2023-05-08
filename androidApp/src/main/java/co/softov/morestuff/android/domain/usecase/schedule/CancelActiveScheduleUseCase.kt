package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.service.Scheduler

interface CancelActiveScheduleUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure,ScheduleDomain>
}

class CancelActiveScheduleUseCaseImpl(
    private val scheduler: Scheduler,
    private val getActiveSchedule: GetActiveScheduleUseCase,
    private val setScheduleFulfilled: SetScheduleFulfilledUseCase
) : CancelActiveScheduleUseCase {

    override suspend fun invoke(taskId: Long): Either<Failure,ScheduleDomain> {
        return getActiveSchedule(taskId).also { result ->
            if (result is Either.Right) {
                val scheduleId = result.value.id
                setScheduleFulfilled(scheduleId)
                scheduler.cancelSchedule(scheduleId)
            }
        }
    }
}



