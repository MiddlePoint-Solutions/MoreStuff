package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import io.middlepoint.morestuff.shared.domain.usecase.priority.GetDefaultPriorityScoreUseCase

interface CreateTaskUseCase {
    suspend operator fun invoke(params: TaskParams): Task
}

data class TaskParams(
    val title: String,
    val priority: Priority,
    val scopeId: Uuid
)

class CreateNewTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val getDefaultPriorityScoreUseCase: GetDefaultPriorityScoreUseCase,
) : CreateTaskUseCase {

    override suspend fun invoke(params: TaskParams): Task = with(params) {
        val priorityScore = getDefaultPriorityScoreUseCase(priority)
        taskRepository.createTask(
            title = title,
            scopeId = scopeId,
            priorityScore = priorityScore
        )
    }
} 



