package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow


interface GetActiveTasksFlowUseCase {
    operator fun invoke(scopeId: Long? = null): Flow<List<TaskDomain>>
}

class GetActiveTasksFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksFlowUseCase {
    override fun invoke(scopeId: Long?): Flow<List<TaskDomain>> {
        return taskRepository.getActiveTasksFlow()
    }
} 



