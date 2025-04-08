package io.middlepoint.morestuff.shared.domain.redux.action

import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.redux.store.Action

sealed class ReminderAction : Action.FeatureAction() {

    data class UserResponseAction(
      val scheduleId: Long,
      val replyType: ReplyType,
    ) : ReminderAction()

}