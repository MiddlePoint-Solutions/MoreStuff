package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.service.Scheduler

interface CancelActiveScheduleUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType> = listOf()
    ): Either<Failure, List<ScheduleDomain>>
}

class CancelActiveScheduleUseCaseImpl(
    private val scheduler: Scheduler,
    private val getActiveSchedule: GetActiveScheduleUseCase,
    private val setScheduleFulfilled: SetScheduleFulfilledUseCase
) : CancelActiveScheduleUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> {
        return getActiveSchedule(taskId, scheduleType).tap { schedules ->
            schedules.forEach {
                setScheduleFulfilled(it.id)
                scheduler.cancelSchedule(it.id)
            }

        }
    }
}



