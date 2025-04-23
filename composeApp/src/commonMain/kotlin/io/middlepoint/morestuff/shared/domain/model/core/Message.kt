package io.middlepoint.morestuff.shared.domain.model.core

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Message(
  val id: Uuid,
  val taskId: Uuid,
  val scheduleId: Uuid?,
  val createdAt: Instant,
  val updatedAt: Instant,
  val contentType: ContentType,
  val content: String,
  val deleted: Boolean,
  val openGraphResult: OpenGraphResult? = null,
  val messageExtra: MessageExtra? = null
)
