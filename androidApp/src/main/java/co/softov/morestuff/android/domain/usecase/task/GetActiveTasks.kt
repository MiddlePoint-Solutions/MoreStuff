package co.softov.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.Scope
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetActiveTasks {
    suspend operator fun invoke(): Flow<List<Scope>>
}

class GetActiveTasksImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasks {
    override suspend fun invoke(): Flow<List<Scope>> {
        return taskRepository.getActiveTasksFlow()
    }
} 



