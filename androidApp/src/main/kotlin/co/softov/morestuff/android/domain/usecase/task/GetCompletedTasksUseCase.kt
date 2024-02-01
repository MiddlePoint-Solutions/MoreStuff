package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetCompletedTasksUseCase {
    suspend operator fun invoke(): Flow<List<TaskDomain>>
}

class GetCompletedTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetCompletedTasksUseCase {
    override suspend fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getCompleteTasksFlow()
    }
} 



