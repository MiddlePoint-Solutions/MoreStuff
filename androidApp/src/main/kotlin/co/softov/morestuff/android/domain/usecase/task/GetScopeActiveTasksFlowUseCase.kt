package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
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
