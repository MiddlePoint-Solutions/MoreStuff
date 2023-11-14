package co.softov.morestuff.android.ui.chat

import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.ui.model.MessageUiModel


data class ChatActions(
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit = { _, _ -> },
    val taskChatAction: (taskId: Long) -> Unit = {},
    val copyMessage: (MessageUiModel) -> Unit = {},
    val deleteMessage: (MessageUiModel) -> Unit = {},
    val onImageSelected: (MessageUiModel) -> Unit = {},
    val shareImage: (imagePath: String) -> Unit = {},
    val shareMessage: (MessageUiModel) -> Unit = {},
)