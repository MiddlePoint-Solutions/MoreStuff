package co.softov.morestuff.androidApp.presentation.content

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.enums.TimeOption

class ChatActions(
    val confirmationAction: (taskId: Long, TimeOption) -> Unit,
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit
    // TODO: Add taskChatAction
)