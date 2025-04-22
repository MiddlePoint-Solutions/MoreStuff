package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.core.ScopeType

typealias ScopeDataMapper = (
    scope_id: Long,
    scope_uid: String,
    scope_name: String,
    scope_order: Int,
    scope_type: Long
) -> ScopeDomain

fun makeScopeDbMapper(): ScopeDataMapper = ::mapScopeDb

fun mapScopeDb(
    scope_id: Long,
    scope_uid: String,
    scope_name: String,
    scope_order: Int,
    scope_type: Long
): ScopeDomain {
    return ScopeDomain(
        id = scope_id,
        uid = scope_uid,
        name = scope_name,
        order = scope_order,
        scopeType = ScopeType.withValue(scope_type.toInt())
    )
}
