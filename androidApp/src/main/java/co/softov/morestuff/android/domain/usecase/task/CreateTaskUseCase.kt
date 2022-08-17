package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.Failure
import arrow.core.Either
import co.softov.morestuff.android.domain.model.Scope
import co.softov.morestuff.android.domain.repository.TaskRepository

interface CreateTaskUseCase {
    suspend operator fun invoke(params: TaskParams): Either<Failure, Scope>
}

data class TaskParams(val title: String)

class CreateNewTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : CreateTaskUseCase {

    override suspend fun invoke(params: TaskParams): Either<Failure, Scope> {
        return taskRepository.createTask(params.title)
    }
} 



