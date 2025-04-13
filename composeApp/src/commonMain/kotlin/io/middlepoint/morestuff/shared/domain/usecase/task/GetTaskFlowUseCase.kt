package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.core.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskFlowUseCase {
    operator fun invoke(taskId: Long): Flow<TaskDomain>
}

class GetTaskFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskFlowUseCase {
    override fun invoke(taskId: Long): Flow<TaskDomain> {
        return taskRepository.getTaskFlow(taskId)
    }
}