package io.middlepoint.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import io.middlepoint.morestuff.android.domain.service.Scheduler
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



