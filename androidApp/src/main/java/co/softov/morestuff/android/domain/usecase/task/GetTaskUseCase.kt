package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Scope
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetTaskUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure,Scope>
}

class GetTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure,Scope> {
        return taskRepository.getTask(taskId)
    }
}