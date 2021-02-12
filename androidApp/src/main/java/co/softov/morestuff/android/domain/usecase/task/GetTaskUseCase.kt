package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.domain.repository.TaskRepository

interface GetTaskUseCase {
    suspend operator fun invoke(taskId: Long): SimpleResult<Task>
}

class GetTaskUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTaskUseCase {
    override suspend fun invoke(taskId: Long): SimpleResult<Task> {
        return taskRepository.getTask(taskId)
    }
}