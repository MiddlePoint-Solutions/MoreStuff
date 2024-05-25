package io.middlepoint.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult

@Immutable
data class MessageUiModel(
  val id: Long = 0,
  val taskId: Long = 0,
  val scheduleId: Long = 0,
  val contentType: ContentType,
  val createTime: String,
  val content: String,
  val replyType: ReplyType? = null,
  val replyContent: String? = null,
  val openGraphResult: OpenGraphResult? = null,
  val messageData: MessageData? = null,
  val formattedTime: String,
  val formattedTimeOnly: String
) {
  val isDataMessage: Boolean get() = messageData != null
  val isPdfMessage: Boolean get() = messageData?.messageType == MessageDataType.Pdf
}