package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.TaskRepository

interface SetTaskCompleteUseCase {
    suspend operator fun invoke(taskIds: List<Long>, complete: Boolean): Either<Failure, Boolean>
}

class SetTaskCompleteImpl(
    private val taskRepository: TaskRepository,
) : SetTaskCompleteUseCase {
    override suspend fun invoke(taskIds: List<Long>, complete: Boolean): Either<Failure, Boolean> {
        return taskRepository.updateTasksComplete(taskIds, complete)
    }
}