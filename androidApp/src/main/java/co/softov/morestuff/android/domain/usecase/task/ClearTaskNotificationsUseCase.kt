package co.softov.morestuff.android.domain.usecase.task

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.repository.TaskRepository
import co.softov.morestuff.android.domain.service.Notifier

interface ClearTaskNotificationsUseCase {
    suspend operator fun invoke(taskId: Long)
}

class ClearTaskNotificationsUseCaseImpl(
    private val notifier: Notifier
) : ClearTaskNotificationsUseCase {
    override suspend fun invoke(taskId: Long) {
        val activeScheduleIds = notifier.getActiveNotificationScheduleIds()
        // TODO: get schedules & check if they belong to the completed task.
    }
}