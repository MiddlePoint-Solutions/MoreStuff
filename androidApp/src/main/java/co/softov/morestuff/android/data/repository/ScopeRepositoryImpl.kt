package co.softov.morestuff.android.data.repository

import co.softov.morestuff.android.data.mapper.DataMappers
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.repository.ScopeRepository
import co.softov.morestuff.db.StuffDb

class ScopeRepositoryImpl(
    database: StuffDb,
    private val dataMappers: DataMappers,
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

    override suspend fun getScopes(): List<ScopeDomain> {
        return scopeQueries.getScope().executeAsList().map { scope ->
            dataMappers.scopeDbMapper(scope.scope_id, scope.scope_uid, scope.name)
        }
    }
    override suspend fun updateScopeName(scopeId: Long, newName: String) {
        scopeQueries.transaction {
            scopeQueries.updateScopeName(newName, scopeId)
        }
    }
    override suspend fun updateScopeOrder(scopeId: Long, newOrderIndex: Long) {
        scopeQueries.transaction {
            scopeQueries.updateScopeOrder(scopeId, newOrderIndex)
        }
    }

}