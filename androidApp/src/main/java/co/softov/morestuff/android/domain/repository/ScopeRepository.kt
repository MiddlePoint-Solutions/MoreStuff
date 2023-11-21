package co.softov.morestuff.android.domain.repository

interface ScopeRepository{
    suspend fun createScope(scopeUid: String, name: String)
    suspend fun deleteScope(scopeIds: List<Long>)
}