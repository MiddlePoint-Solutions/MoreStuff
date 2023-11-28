package co.softov.morestuff.android.domain.repository

import co.softov.morestuff.android.domain.model.ScopeDomain

interface ScopeRepository{
    suspend fun createScope(scopeUid: String, name: String)
    suspend fun deleteScope(scopeIds: List<Long>)
    suspend fun getScopes(): List<ScopeDomain>
}