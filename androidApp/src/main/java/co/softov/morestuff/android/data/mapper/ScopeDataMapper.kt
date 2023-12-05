package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.ScopeDomain

typealias ScopeDataMapper = (
    scope_id: Long,
    scope_uid: String,
    scope_name: String,
    scope_order: Int,
) -> ScopeDomain

fun makeScopeDbMapper(): ScopeDataMapper = ::mapScopeDb

fun mapScopeDb(
    scope_id: Long,
    scope_uid: String,
    scope_name: String,
    scope_order: Int,
): ScopeDomain {
    return ScopeDomain(
        id = scope_id,
        uid = scope_uid,
        name = scope_name,
        order = scope_order,
    )
}
