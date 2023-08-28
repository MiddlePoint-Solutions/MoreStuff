package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetActiveTasksUseCase {
    operator fun invoke(): Flow<List<TaskDomain>>
}

class GetActiveTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getActiveTasksFlow()
    }
} 



