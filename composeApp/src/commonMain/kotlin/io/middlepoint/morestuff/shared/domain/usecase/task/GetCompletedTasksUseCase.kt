package io.middlepoint.morestuff.shared.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import io.middlepoint.morestuff.shared.domain.model.core.Task
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

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



