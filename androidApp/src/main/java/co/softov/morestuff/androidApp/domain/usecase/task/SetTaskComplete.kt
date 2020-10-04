package co.softov.morestuff.androidApp.domain.usecase.task

import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.TaskRepository
import co.softov.morestuff.androidApp.domain.usecase.schedule.CancelActiveScheduleForTask

interface SetTaskComplete {
    suspend operator fun invoke(taskId: Long): SimpleResult<Boolean>
}

class SetTaskCompleteImpl(
    private val taskRepository: TaskRepository,
    private val cancelActiveTaskSchedule: CancelActiveScheduleForTask
) : SetTaskComplete {
    override suspend fun invoke(taskId: Long): SimpleResult<Boolean> {
        cancelActiveTaskSchedule(taskId)
        return taskRepository.setTaskComplete(taskId)
    }
}