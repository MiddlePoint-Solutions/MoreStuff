package co.softov.morestuff.android.data.repository

import co.softov.morestuff.android.domain.repository.ScopeRepository
import co.softov.morestuff.db.StuffDb

class ScopeRepositoryImpl(
    database: StuffDb,
) : ScopeRepository {
    private val scopeQueries = database.scopeQueries


    override suspend fun createScope(scopeUid: String, name: String) {
        scopeQueries.transaction {
            scopeQueries.createScope(scopeUid, name)
        }
    }
    override suspend fun deleteScope(scopeIds: List<Long>) {
        scopeQueries.transaction {
            scopeQueries.deleteScope(scopeIds)
        }
    }

}