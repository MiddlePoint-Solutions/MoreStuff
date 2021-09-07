package co.softov.morestuff.android.ui.main

import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType


class ChatActions(
    val confirmationAction: (taskId: Long, PriorityOption) -> Unit,
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit
    // TODO: Add taskChatAction
)