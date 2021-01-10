package co.softov.morestuff.androidApp.presentation.content

import co.softov.morestuff.androidApp.domain.enums.PriorityOption
import co.softov.morestuff.androidApp.domain.enums.ReplyType


class ChatActions(
    val confirmationAction: (taskId: Long, PriorityOption) -> Unit,
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit
    // TODO: Add taskChatAction
)