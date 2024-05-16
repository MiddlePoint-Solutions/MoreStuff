package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.enums.TaskType
import io.middlepoint.morestuff.android.domain.model.Priority
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
import io.middlepoint.morestuff.android.domain.usecase.priority.GetDefaultPriorityScoreUseCase

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



