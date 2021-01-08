package co.softov.morestuff.androidApp.presentation.content

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.PriorityOption
import co.softov.morestuff.androidApp.domain.model.TimeOption


class ChatActions(
    val confirmationAction: (taskId: Long, PriorityOption) -> Unit,
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit
    // TODO: Add taskChatAction
)