@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.ScopeSync
import io.middlepoint.morestuff.shared.data.sync.Sync
import io.middlepoint.morestuff.shared.data.sync.TaskSync
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import kotlinx.datetime.Instant

typealias ScopeDataMapper<R> = (
  id: Uuid,
  scope_name: String,
  scope_order: Int,
  created_at: Instant,
  updated_at: Instant,
  deleted: Boolean
) -> R

fun makeScopeDataMapper(): ScopeDataMapper<Scope> = ::mapScopeData
fun makeScopeSyncMapper(): ScopeDataMapper<Sync<ScopeSync>> = ::mapScopeSync

fun mapScopeData(
  id: Uuid,
  scope_name: String,
  scope_order: Int,
  created_at: Instant,
  updated_at: Instant,
  deleted: Boolean
): Scope {
  return Scope(
    id = id,
    name = scope_name,
    order = scope_order,
    createdAt = created_at,
    updatedAt = updated_at,
    deleted = deleted
  )
}

fun mapScopeSync(
  id: Uuid,
  scope_name: String,
  scope_order: Int,
  created_at: Instant,
  updated_at: Instant,
  deleted: Boolean
) = Sync(
  id = id,
  createdAt = created_at,
  updatedAt = updated_at,
  deleted = deleted,
  data = ScopeSync(id, scope_name, scope_order, created_at, updated_at, deleted)
)
