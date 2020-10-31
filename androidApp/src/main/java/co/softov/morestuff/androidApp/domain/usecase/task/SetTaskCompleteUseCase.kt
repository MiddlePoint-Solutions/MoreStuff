package co.softov.morestuff.androidApp.domain.usecase.task

import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.TaskRepository
import co.softov.morestuff.androidApp.domain.usecase.schedule.CancelActiveScheduleUseCase

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