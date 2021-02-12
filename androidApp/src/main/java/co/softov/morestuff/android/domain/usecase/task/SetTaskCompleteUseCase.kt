package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.TaskRepository

interface SetTaskCompleteUseCase {
    suspend operator fun invoke(taskId: Long): SimpleResult<Boolean>
}

class SetTaskCompleteImpl(
    private val taskRepository: TaskRepository
) : SetTaskCompleteUseCase {
    override suspend fun invoke(taskId: Long): SimpleResult<Boolean> {
        return taskRepository.setTaskComplete(taskId)
    }
}