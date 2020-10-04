package co.softov.morestuff.androidApp.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task
import co.softov.morestuff.androidApp.domain.repository.TaskRepository

interface GetActiveTasks {
    suspend operator fun invoke(): SimpleResult<Flow<List<Task>>>
}

class GetActiveTasksImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasks {
    override suspend fun invoke(): SimpleResult<Flow<List<Task>>> {
        return taskRepository.getActiveTasksFlow()
    }
} 



