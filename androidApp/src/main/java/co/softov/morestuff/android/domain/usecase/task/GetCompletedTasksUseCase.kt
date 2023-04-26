package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetCompletedTasksUseCase {
    suspend operator fun invoke(): Flow<List<Task>>
}

class GetCompletedTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetCompletedTasksUseCase {
    override suspend fun invoke(): Flow<List<Task>> {
        return taskRepository.getCompleteTasksFlow()
    }
} 



