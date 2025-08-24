package io.middlepoint.morestuff.shared.domain.model.core.legacy

import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import kotlinx.serialization.Serializable

@Serializable
data class LegacyMessageData(
  val id: Long,
  val filePath: String,
  val creationTime: String,
  val messageType: MessageExtraType
)