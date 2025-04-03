package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface GetTasksByIdsUseCase {
    suspend operator fun invoke(taskIds: List<Long>): Either<Failure, List<TaskDomain>>
}

class GetTasksByIdsUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTasksByIdsUseCase {
    override suspend fun invoke(taskIds: List<Long>): Either<Failure, List<TaskDomain>> {
        return taskRepository.getTasksByIds(taskIds)
    }
}