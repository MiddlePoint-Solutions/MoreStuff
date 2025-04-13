package io.middlepoint.morestuff.shared.domain.model.core.migrate

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult

data class MessageNew(
  val id: String = "",
  val taskId: String = "",
  val scheduleId: Long = 0,
  val contentType: ContentType,
  val createTime: String,
  val seenTime: String? = null,
  val content: String,
  val replyType: ReplyType? = null,
  val replyContent: String? = null,
  val replyTime: String? = null,
  val openGraphResult: OpenGraphResult? = null,
  val messageData: MessageData? = null
)
