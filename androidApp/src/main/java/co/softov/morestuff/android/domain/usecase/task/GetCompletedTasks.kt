package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.Scope
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetCompletedTasks {
    suspend operator fun invoke(): Flow<List<Scope>>
}

class GetCompletedTasksImpl(
    private val taskRepository: TaskRepository
) : GetCompletedTasks {
    override suspend fun invoke(): Flow<List<Scope>> {
        return taskRepository.getCompleteTasksFlow()
    }
} 



