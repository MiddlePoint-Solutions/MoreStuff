package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetTaskUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure,Task>
}

class GetTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure,Task> {
        return taskRepository.getTask(taskId)
    }
}