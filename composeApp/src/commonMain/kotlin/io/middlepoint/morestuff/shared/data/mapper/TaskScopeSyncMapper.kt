@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.TaskScopeSync
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant

typealias TaskScopeSyncMapper = (
  task_id: Uuid,
  scope_id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  deleted: Boolean,
) -> TaskScopeSync

fun makeTaskScopeSyncMapper(): TaskScopeSyncMapper = ::mapTaskScopeSync

fun mapTaskScopeSync(
  task_id: Uuid,
  scope_id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  deleted: Boolean,
) = TaskScopeSync(
  task_id,
  scope_id,
  created_at,
  updated_at,
  deleted
)