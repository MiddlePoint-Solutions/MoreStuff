package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

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
