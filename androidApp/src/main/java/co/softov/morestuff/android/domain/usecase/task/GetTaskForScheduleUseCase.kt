package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase

interface GetTaskForScheduleUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,TaskDomain>
}

class GetTaskForScheduleUseCaseImpl(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : GetTaskForScheduleUseCase {
    override suspend fun invoke(scheduleId: Long): Either<Failure,TaskDomain> {
        return when (val result = getScheduleUseCase(scheduleId)) {
            is Either.Right -> getTaskUseCase(result.value.taskId)
            is Either.Left -> result
        }
    }
}