package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.PriorityRepository

interface UpdateTaskPriorityScoreUseCase {
    suspend operator fun invoke(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long>
}

class UpdateTaskPriorityScoreUseCaseImpl(
    private val priorityRepository: PriorityRepository,
) : UpdateTaskPriorityScoreUseCase {
    override suspend operator fun invoke(
        taskId: Uuid,
        priorityScore: Long,
    ): Either<Failure, Long> {
        return priorityRepository.updateTaskPriority(taskId, priorityScore)
    }
}