package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface DeleteTasksUseCase {
    suspend operator fun invoke(taskIds: List<Long>): Either<Failure, Boolean>
}

class DeleteTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : DeleteTasksUseCase {
    override suspend fun invoke(taskIds: List<Long>): Either<Failure, Boolean> {
        return taskRepository.deleteTasks(taskIds)
    }
}
