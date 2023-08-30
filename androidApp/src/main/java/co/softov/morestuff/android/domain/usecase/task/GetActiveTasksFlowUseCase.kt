package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetActiveTasksFlowUseCase {
    operator fun invoke(): Flow<List<TaskDomain>>
}

class GetActiveTasksFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksFlowUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getActiveTasksFlow()
    }
} 



