package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.Failure
import arrow.core.Either
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface CreateTaskUseCase {
    suspend operator fun invoke(params: TaskParams): Either<Failure, TaskDomain>
}

data class TaskParams(
    val title: String,
    val priorityScore: Long,
    val taskType: TaskType,
)

class CreateNewTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : CreateTaskUseCase {

    override suspend fun invoke(params: TaskParams): Either<Failure, TaskDomain> = with(params) {
        taskRepository.createTask(title, priorityScore, taskType)
    }
} 



