package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase

interface ClearTaskNotificationsUseCase {
    suspend operator fun invoke(taskId: Long)
}

class ClearTaskNotificationsUseCaseImpl(
    private val notifier: Notifier,
    private val getScheduleUseCase: GetScheduleUseCase,
) : ClearTaskNotificationsUseCase {
    override suspend fun invoke(taskId: Long) {
        val activeScheduleIds = notifier.getActiveNotificationScheduleIds()
        getScheduleUseCase(activeScheduleIds).tap {
            it.forEach { schedule ->
                if (schedule.taskId == taskId) {
                    notifier.clearScheduleNotification(schedule.id)
                }
            }
        }
    }
}