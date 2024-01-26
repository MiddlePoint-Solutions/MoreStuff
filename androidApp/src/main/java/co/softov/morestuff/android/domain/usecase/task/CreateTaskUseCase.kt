package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.usecase.priority.GetDefaultPriorityScoreUseCase

interface CreateTaskUseCase {
    suspend operator fun invoke(params: TaskParams): TaskDomain
}

data class TaskParams(
    val title: String,
    val priority: Priority,
    val taskType: TaskType,
    val scopeId: Long
)

class CreateNewTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
) : CreateTaskUseCase {

    override suspend fun invoke(params: TaskParams): TaskDomain = with(params) {
        val priorityScore = getDefaultPriorityScoreUseCase(priority)
        taskRepository.createTask(
            title = title,
            priorityScore = priorityScore,
            taskType = taskType,
            scopeId = scopeId
        )
    }
} 



