package io.middlepoint.morestuff.android.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository

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



