package co.softov.morestuff.android.ui.chat

import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.enums.ReplyType


class ChatActions(
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit = {_, _ ->},
    val taskChatAction: (taskId: Long) -> Unit = {},
)