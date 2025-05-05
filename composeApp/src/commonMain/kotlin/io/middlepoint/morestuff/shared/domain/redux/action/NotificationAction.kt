package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class NotificationAction : Action.FeatureAction() {
  internal data class ShowReminderNotificationAction(
    val message: Message,
  ) : NotificationAction()

  data object ShowReviewNotification : NotificationAction()

  internal data class RemoveScheduleNotificationAction(
    val scheduleId: Uuid,
  ) : NotificationAction()

  data class UserResponseAction(
    val scheduleId: Uuid,
    val replyType: ReplyType,
  ) : NotificationAction()
}