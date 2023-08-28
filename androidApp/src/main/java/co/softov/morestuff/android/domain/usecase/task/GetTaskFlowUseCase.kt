package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskFlowUseCase {
    operator fun invoke(taskId: Long): Flow<TaskDomain>
}

class GetTaskFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskFlowUseCase {
    override fun invoke(taskId: Long): Flow<TaskDomain> {
        return taskRepository.getTaskFlow(taskId)
    }
}