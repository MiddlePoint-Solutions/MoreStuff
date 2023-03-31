package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.usecase.BaseUseCase

interface RescheduleTaskUseCase : BaseUseCase<Failure, RescheduleTaskUseCaseParams, Schedule>

data class RescheduleTaskUseCaseParams(
    val taskId: Long,
    val priority: Priority,
)

class RescheduleTaskUseCaseImpl(
    private val cancelActiveScheduleUseCase: CancelActiveScheduleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase
) : RescheduleTaskUseCase {

    override suspend fun invoke(params: RescheduleTaskUseCaseParams): Either<Failure, Schedule> {
        cancelActiveScheduleUseCase(params.taskId)
        return createScheduleUseCase(params.taskId, params.priority)
    }

}