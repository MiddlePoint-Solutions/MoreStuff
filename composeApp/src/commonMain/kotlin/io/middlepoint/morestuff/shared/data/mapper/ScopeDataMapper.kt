@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.domain.model.core.Scope

typealias ScopeDataMapper = (
    scope_id: Long,
    scope_uid: String,
    scope_name: String,
    scope_order: Int,
) -> Scope

fun makeScopeDbMapper(): ScopeDataMapper = ::mapScopeDb

fun mapScopeDb(
    scope_id: Long,
    scope_uid: String,
    scope_name: String,
    scope_order: Int,
): Scope {
    return Scope(
        id = scope_id,
        uid = scope_uid,
        name = scope_name,
        order = scope_order,
    )
}
