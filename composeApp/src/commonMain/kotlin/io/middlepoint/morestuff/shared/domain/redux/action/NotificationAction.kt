package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class NotificationAction : Action.FeatureAction() {
    internal data class ShowReminderNotificationAction(
      val message: Message,
    ) : NotificationAction()

    data object ShowReviewNotification : NotificationAction()

    internal data class RemoveScheduleNotificationAction(
        val scheduleId: Long,
    ) : NotificationAction()
}