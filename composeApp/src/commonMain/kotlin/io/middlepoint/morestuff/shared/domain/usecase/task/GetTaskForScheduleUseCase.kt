package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase

interface GetTaskForScheduleUseCase {
    suspend operator fun invoke(scheduleId: Uuid): Either<Failure, Task>
}

class GetTaskForScheduleUseCaseImpl(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getTaskUseCase: GetTaskUseCase
) : GetTaskForScheduleUseCase {
    override suspend fun invoke(scheduleId: Uuid): Either<Failure, Task> {
        return when (val result = getScheduleUseCase.invoke(scheduleId)) {
            is Either.Right -> getTaskUseCase(result.value.taskId)
            is Either.Left -> result
        }
    }
}