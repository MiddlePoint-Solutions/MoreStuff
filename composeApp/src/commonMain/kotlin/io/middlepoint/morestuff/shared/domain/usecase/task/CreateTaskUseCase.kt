package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.core.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetDefaultPriorityScoreUseCase

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



