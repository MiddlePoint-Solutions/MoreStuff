package io.middlepoint.morestuff.shared.ui.screen.chat

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel

@Immutable
data class ChatActions(
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit = { _, _ -> },
    val taskChatAction: (taskId: Long) -> Unit = {},
    val copyMessage: (MessageUiModel) -> Unit = {},
    val deleteMessage: (MessageUiModel) -> Unit = {},
    val onImageSelected: (MessageUiModel) -> Unit = {},
    val onPdfSelected: (MessageUiModel) -> Unit = {},
    val shareImage: (imagePath: String) -> Unit = {},
    val sharePdf: (pdfPath: String) -> Unit = {},
    val shareMessage: (MessageUiModel) -> Unit = {},
    val setEditingMessage: (Long) -> Unit = {},
    val updateMessageContent: (String) -> Unit = {},
    val isMessageBeingEdited: (Long) -> Boolean = { false }
)