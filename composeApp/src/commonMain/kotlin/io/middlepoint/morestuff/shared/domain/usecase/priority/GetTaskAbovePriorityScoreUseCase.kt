package io.middlepoint.morestuff.shared.domain.usecase.priority

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface GetTaskAbovePriorityScoreUseCase {

    suspend operator fun invoke(priorityScore: Long): Either<Failure, TaskDomain>

}

class GetTaskAbovePriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskAbovePriorityScoreUseCase {
    override suspend fun invoke(priorityScore: Long): Either<Failure, TaskDomain> {
        return taskRepository.getTaskAbovePriorityScore(priorityScore)
    }

}