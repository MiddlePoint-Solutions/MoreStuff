package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetCompletedTasks {
    suspend operator fun invoke(): SimpleResult<Flow<List<Task>>>
}

class GetCompletedTasksImpl(
    private val taskRepository: TaskRepository
) : GetCompletedTasks {
    override suspend fun invoke(): SimpleResult<Flow<List<Task>>> {
        return taskRepository.getCompleteTasksFlow()
    }
} 



