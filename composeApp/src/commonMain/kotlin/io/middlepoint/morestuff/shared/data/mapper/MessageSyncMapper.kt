@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.MessageSync
import io.middlepoint.morestuff.shared.data.sync.Sync
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant

typealias MessageSyncMapper = (
  id: Uuid,
  taskId: Uuid,
  scheduleId: Uuid?,
  created_at: Instant,
  updated_at: Instant,
  contentType: Int,
  content: String,
  deleted: Boolean
) -> Sync<MessageSync>

fun makeMessageSyncMapper(): MessageSyncMapper = ::mapMessageSync

fun mapMessageSync(
  id: Uuid,
  taskId: Uuid,
  scheduleId: Uuid?,
  created_at: Instant,
  updated_at: Instant,
  contentType: Int,
  content: String,
  deleted: Boolean
) = Sync(
  id = id,
  createdAt = created_at,
  updatedAt = updated_at,
  deleted = deleted,
  data = MessageSync(
    id,
    taskId,
    scheduleId,
    created_at,
    updated_at,
    contentType,
    content,
    deleted
  )
)