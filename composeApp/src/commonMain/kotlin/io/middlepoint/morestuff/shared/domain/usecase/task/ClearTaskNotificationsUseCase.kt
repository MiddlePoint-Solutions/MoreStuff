package io.middlepoint.morestuff.shared.domain.usecase.task

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.usecase.schedule.GetScheduleUseCase

interface ClearTaskNotificationsUseCase {
  suspend operator fun invoke(taskIds: List<Uuid>)
}

class ClearTaskNotificationsUseCaseImpl(
  private val notifier: Notifier,
  private val getScheduleUseCase: GetScheduleUseCase,
) : ClearTaskNotificationsUseCase {
  override suspend fun invoke(taskIds: List<Uuid>) {
    val activeScheduleIds = notifier.getActiveNotificationScheduleIds()
    // TODO: after refactoring to use string ids, in Android we could create a new table that will connect schedules to an integer id, this way we can use the following code
//        getScheduleUseCase(activeScheduleIds).onRight {
//            it.forEach { schedule ->
//                if (schedule.taskId in taskIds) {
//                    notifier.clearScheduleNotification(schedule.id)
//                }
//            }
//        }
  }
}