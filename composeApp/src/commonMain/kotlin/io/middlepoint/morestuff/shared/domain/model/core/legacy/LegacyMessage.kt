package io.middlepoint.morestuff.shared.domain.model.core.legacy

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import kotlinx.serialization.Serializable

@Serializable
data class LegacyMessage(
  val id: Long = 0,
  val taskId: Long = 0,
  val scheduleId: Long = 0,
  val contentType: ContentType,
  val createTime: String,
  val seenTime: String? = null,
  val content: String,
  val replyType: ReplyType? = null,
  val replyContent: String? = null,
  val replyTime: String? = null,
  val openGraphResult: OpenGraphResult? = null,
  val messageData: LegacyMessageData? = null
)