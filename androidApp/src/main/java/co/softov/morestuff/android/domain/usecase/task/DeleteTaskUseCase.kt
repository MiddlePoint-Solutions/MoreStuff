package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface DeleteTaskUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Boolean>
}

class DeleteTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : DeleteTaskUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, Boolean> {
        return taskRepository.deleteTask(taskId)
    }
}
