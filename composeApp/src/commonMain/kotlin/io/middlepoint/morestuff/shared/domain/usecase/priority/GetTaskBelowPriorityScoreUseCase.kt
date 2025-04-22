package io.middlepoint.morestuff.shared.domain.usecase.priority

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface GetTaskBelowPriorityScoreUseCase {

    suspend operator fun invoke(priorityScore: Long): Either<Failure, Task>

}

class GetTaskBelowPriorityScoreUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskBelowPriorityScoreUseCase {
    override suspend fun invoke(priorityScore: Long): Either<Failure, Task> {
        return taskRepository.getTaskBelowPriorityScore(priorityScore)
    }

}