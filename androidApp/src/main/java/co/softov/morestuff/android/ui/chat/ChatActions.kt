package co.softov.morestuff.android.ui.chat

import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message


data class ChatActions(
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit = { _, _ -> },
    val taskChatAction: (taskId: Long) -> Unit = {},
    val copyMessage: (Message) -> Unit = {},
    val deleteMessage: (Message) -> Unit = {},
    val onImageSelected: (Message) -> Unit = {},
    val shareImage: (imagePath: String) -> Unit = {},
    val shareMessage: (Message) -> Unit = {},
)