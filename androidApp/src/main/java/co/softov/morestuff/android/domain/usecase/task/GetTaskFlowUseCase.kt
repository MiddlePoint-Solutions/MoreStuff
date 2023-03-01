package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskFlowUseCase {
    operator fun invoke(taskId: Long): Flow<Task>
}

class GetTaskFlowUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskFlowUseCase {
    override fun invoke(taskId: Long): Flow<Task> {
        return taskRepository.getTaskFlow(taskId)
    }
}