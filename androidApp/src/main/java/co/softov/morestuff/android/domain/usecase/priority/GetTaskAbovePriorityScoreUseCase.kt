package co.softov.morestuff.android.domain.usecase.priority

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

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