package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.ScopeDomain

typealias ScopeDb = co.softov.morestuff.db.Scope
typealias ScopeDataMapper = (
    scope_id: Long,
    scope_uid: String,
    name: String
) -> ScopeDomain

fun makeScopeDbMapper(): ScopeDataMapper = ::mapScopeDb

fun mapScopeDb(
    scope_id: Long,
    scope_uid: String,
    name: String
): ScopeDomain {
    return ScopeDomain(
        scopeId = scope_id,
        uid = scope_uid,
        name = name
    )
}
