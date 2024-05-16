package io.middlepoint.morestuff.android.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.repository.PriorityRepository
import io.middlepoint.morestuff.android.domain.repository.TaskRepository

interface UpdateTaskPriorityScoreUseCase {
    suspend operator fun invoke(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long>
}

class UpdateTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
) : UpdateTaskPriorityScoreUseCase {
    override suspend operator fun invoke(
        taskId: Long,
        priorityScore: Long,
    ): Either<Failure, Long> {
        return priorityRepository.updateTaskPriority(taskId, priorityScore)
    }
}