package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskFlowUseCase {
    operator fun invoke(taskId: Uuid): Flow<Task>
}

class GetTaskFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskFlowUseCase {
    override fun invoke(taskId: Uuid): Flow<Task> {
        return taskRepository.getTaskFlow(taskId)
    }
}