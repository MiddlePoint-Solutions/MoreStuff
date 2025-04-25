package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.model.core.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.Uuid

@Immutable
data class MessageUiModel(
  val id: Uuid,
  val taskId: Uuid,
  val scheduleId: Uuid?,
  val contentType: ContentType,
  val createAt: String,
  val content: String,
  val openGraphResult: OpenGraphResult? = null,
  val messageExtra: MessageExtra? = null,
  val formattedTime: String,
  val formattedTimeOnly: String
)

val MessageUiModel.isDataMessage: Boolean get() = messageExtra != null
val MessageUiModel.isPdfMessage: Boolean get() = messageExtra?.messageType == MessageExtraType.Pdf

