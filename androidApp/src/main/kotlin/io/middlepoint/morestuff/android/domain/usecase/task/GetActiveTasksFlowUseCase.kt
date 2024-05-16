package io.middlepoint.morestuff.android.domain.usecase.task

import io.middlepoint.morestuff.android.domain.model.TaskDomain
import io.middlepoint.morestuff.android.domain.repository.TaskRepository
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
