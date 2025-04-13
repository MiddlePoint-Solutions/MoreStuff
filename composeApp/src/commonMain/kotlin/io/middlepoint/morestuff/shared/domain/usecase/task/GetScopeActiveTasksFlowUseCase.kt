package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow


interface GetScopeActiveTasksFlowUseCase {
    operator fun invoke(scopeId: Long): Flow<List<Task>>
}

class GetScopeActiveTasksFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetScopeActiveTasksFlowUseCase {
    override fun invoke(scopeId: Long): Flow<List<Task>> {
        return taskRepository.getScopeActiveTasksFlow(scopeId)
    }
}
