package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface SetTasksCompleteUseCase {
    suspend operator fun invoke(taskIds: List<Long>, complete: Boolean): Either<Failure, Boolean>
}

class SetTasksCompleteImpl(
    private val taskRepository: TaskRepository
) : SetTasksCompleteUseCase {
    override suspend fun invoke(taskIds: List<Long>, complete: Boolean): Either<Failure, Boolean> {
        return taskRepository.updateTasksComplete(taskIds, complete)
    }
}