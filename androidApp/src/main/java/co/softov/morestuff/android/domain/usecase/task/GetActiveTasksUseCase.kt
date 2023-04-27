package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetActiveTasksUseCase {
    suspend operator fun invoke(): Flow<List<Task>>
}

class GetActiveTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksUseCase {
    override suspend fun invoke(): Flow<List<Task>> {
        return taskRepository.getActiveTasksFlow()
    }
} 



