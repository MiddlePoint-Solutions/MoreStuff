package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetActiveTasksFlowUseCase {
    operator fun invoke(): Flow<List<TaskDomain>>
}

class GetActiveTasksFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetActiveTasksFlowUseCase {
    override fun invoke(): Flow<List<TaskDomain>> {
        return taskRepository.getActiveTasksFlow()
    }
}
