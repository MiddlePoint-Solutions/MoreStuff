@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.MessageExtraSync
import io.middlepoint.morestuff.shared.data.sync.MessageRelationSync
import io.middlepoint.morestuff.shared.data.sync.MessageSync
import io.middlepoint.morestuff.shared.data.sync.Sync
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlin.time.Instant

typealias MessageExtraSyncMapper = (
  id: Uuid,
  message_id: Uuid,
  url: String,
  created_at: Instant,
  updated_at: Instant,
  data_type: String,
  deleted: Boolean,
) -> MessageRelationSync<MessageExtraSync>

fun makeMessageExtraSyncMapper(): MessageExtraSyncMapper = ::mapMessageExtraSync

fun mapMessageExtraSync(
  id: Uuid,
  message_id: Uuid,
  url: String,
  created_at: Instant,
  updated_at: Instant,
  data_type: String,
  deleted: Boolean,
) = MessageRelationSync(
  id = id,
  messageId = message_id,
  createdAt = created_at,
  updatedAt = updated_at,
  deleted = deleted,
  data = MessageExtraSync(
    id,
    message_id,
    url,
    created_at,
    updated_at,
    data_type,
    deleted
  )
)