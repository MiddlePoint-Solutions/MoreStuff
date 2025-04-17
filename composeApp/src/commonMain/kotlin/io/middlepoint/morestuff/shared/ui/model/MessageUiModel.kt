package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
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
  val messageExtra: MessageExtra? = null,
  val formattedTime: String,
  val formattedTimeOnly: String
)

val MessageUiModel.isDataMessage: Boolean get() = messageExtra != null
val MessageUiModel.isPdfMessage: Boolean get() = messageExtra?.messageType == MessageExtraType.Pdf

