package io.middlepoint.morestuff.shared.domain.usecase.task

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

interface UpdateTaskTitleUseCase {
    suspend operator fun invoke(taskId: Long, title: String): Either<Failure, Boolean>
}

class UpdateTaskTitleUseCaseImpl(
    private val taskRepository: TaskRepository
) : UpdateTaskTitleUseCase {
    override suspend fun invoke(taskId: Long, title: String): Either<Failure, Boolean> {
        return taskRepository.updateTaskTitle(taskId, title)
    }
}