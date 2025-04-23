@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import kotlinx.datetime.Instant

typealias ScopeDataMapper = (
  id: Uuid,
  scope_name: String,
  scope_order: Int,
  created_at: Instant,
  updated_at: Instant,
  deleted: Boolean
) -> Scope

fun makeScopeDbMapper(): ScopeDataMapper = ::mapScopeDb

fun mapScopeDb(
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
