package co.softov.morestuff.androidApp.domain.usecase.task

import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.model.Task
import co.softov.morestuff.androidApp.domain.repository.TaskRepository

interface GetTask {
    suspend operator fun invoke(taskId: Long): SimpleResult<Task>
}

class GetTaskImpl(
    private val taskRepository: TaskRepository
) : GetTask {
    override suspend fun invoke(taskId: Long): SimpleResult<Task> {
        return taskRepository.getTask(taskId)
    }
}