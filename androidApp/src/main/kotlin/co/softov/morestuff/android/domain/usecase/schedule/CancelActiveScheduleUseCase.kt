package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.service.Scheduler
import timber.log.Timber

interface CancelActiveScheduleUseCase {
    suspend operator fun invoke(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType> = listOf()
    ): Either<Failure, List<ScheduleDomain>>
}

class CancelActiveScheduleUseCaseImpl(
    private val scheduler: Scheduler,
    private val getActiveSchedule: GetActiveSchedulesUseCase,
    private val setScheduleFulfilled: SetScheduleFulfilledUseCase
) : CancelActiveScheduleUseCase {

    override suspend fun invoke(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> {
        return getActiveSchedule(taskIds, scheduleType).onRight { schedules ->
            Timber.d("TEST: $taskIds, $scheduleType schedules ${schedules.map { it.taskId }}")
            schedules.forEach {
                setScheduleFulfilled(it.id)
                scheduler.cancelSchedule(it.id)
            }
        }
    }
}



