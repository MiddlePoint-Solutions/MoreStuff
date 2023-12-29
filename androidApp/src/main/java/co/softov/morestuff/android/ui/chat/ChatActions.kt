package co.softov.morestuff.android.ui.chat

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.ui.model.MessageUiModel

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
)