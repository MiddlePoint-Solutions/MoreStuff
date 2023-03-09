package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

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