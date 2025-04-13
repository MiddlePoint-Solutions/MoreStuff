package io.middlepoint.morestuff.shared.domain.usecase.task

import kotlinx.coroutines.flow.Flow
import io.middlepoint.morestuff.shared.domain.model.core.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository

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



