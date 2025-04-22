package io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.ui.model.IAMessageUiModel
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel

@Immutable
data class ScopeChatActions(
    val scheduleAction: (scheduleId: Long, ReplyType) -> Unit = { _, _ -> },
    val taskChatAction: (taskId: Long) -> Unit = {},
    val copyMessage: (IAMessageUiModel) -> Unit = {},
    val deleteMessage: (IAMessageUiModel) -> Unit = {},
    val onImageSelected: (IAMessageUiModel) -> Unit = {},
    val onPdfSelected: (IAMessageUiModel) -> Unit = {},
    val shareImage: (imagePath: String) -> Unit = {},
    val sharePdf: (pdfPath: String) -> Unit = {},
    val shareMessage: (IAMessageUiModel) -> Unit = {},
    val setEditingMessage: (Long) -> Unit = {},
    val updateMessageContent: (String) -> Unit = {},
    val isMessageBeingEdited: (Long) -> Boolean = { false }
)