package co.softov.morestuff.android.domain.usecase.task

import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.usecase.schedule.GetScheduleUseCase

interface ClearTaskNotificationsUseCase {
    suspend operator fun invoke(taskIds: List<Long>)
}

class ClearTaskNotificationsUseCaseImpl(
    private val notifier: Notifier,
    private val getScheduleUseCase: GetScheduleUseCase,
) : ClearTaskNotificationsUseCase {
    override suspend fun invoke(taskIds: List<Long>) {
        val activeScheduleIds = notifier.getActiveNotificationScheduleIds()
        getScheduleUseCase(activeScheduleIds).onRight {
            it.forEach { schedule ->
                if (schedule.taskId in taskIds) {
                    notifier.clearScheduleNotification(schedule.id)
                }
            }
        }
    }
}