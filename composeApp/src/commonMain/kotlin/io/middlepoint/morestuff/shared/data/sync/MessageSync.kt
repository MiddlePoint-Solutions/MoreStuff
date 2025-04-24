package io.middlepoint.morestuff.shared.data.sync

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import kotlinx.serialization.Serializable

@Serializable
data class MessageSync(
  val id: String,
  val taskId: String,
  val scheduleId: String? = null,
  val messageDataId: String? = null,
  val contentType: ContentType,
  val createTime: String,
  val seenTime: String? = null,
  val content: String,
)
