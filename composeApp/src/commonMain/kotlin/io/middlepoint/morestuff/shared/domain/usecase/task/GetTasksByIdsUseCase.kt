package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface GetTasksByIdsUseCase {
    suspend operator fun invoke(taskIds: List<Uuid>): Either<Failure, List<Task>>
}

class GetTasksByIdsUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTasksByIdsUseCase {
    override suspend fun invoke(taskIds: List<Uuid>): Either<Failure, List<Task>> {
        return taskRepository.getTasksByIds(taskIds)
    }
}