package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow


interface GetScopeActiveTasksFlowUseCase {
    operator fun invoke(scopeId: Long): Flow<List<TaskDomain>>
}

class GetScopeActiveTasksFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetScopeActiveTasksFlowUseCase {
    override fun invoke(scopeId: Long): Flow<List<TaskDomain>> {
        return taskRepository.getScopeActiveTasksFlow(scopeId)
    }
}
