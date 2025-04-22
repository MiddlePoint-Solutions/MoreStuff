package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult

@Immutable
data class IAMessageUiModel(
    val id: Long = 0,
    val scopeId: Long = 0,
    val contentType: ContentType,
    val createTime: String,
    val seenTime: String? = null,
    val content: String,
    val replyType: ReplyType? = null,
    val replyContent: String? = null,
    val replyTime: String? = null,
    val openGraphResult: OpenGraphResult? = null,
    val messageData: MessageData? = null,
    val formattedTime: String,
    val formattedTimeOnly: String
)

val IAMessageUiModel.isDataMessage: Boolean get() = messageData != null
val IAMessageUiModel.isPdfMessage: Boolean get() = messageData?.messageType?.name == "Pdf"