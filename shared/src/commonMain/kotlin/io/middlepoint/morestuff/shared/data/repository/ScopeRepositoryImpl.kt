package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.generateUUID
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ScopeRepositoryImpl(
    database: StuffDb,
    private val dataMappers: DataMappers,
) : ScopeRepository {

    private val scopeQueries = database.scopeQueries
    private val taskQueries = database.taskQueries
    private val taskScopeQueries = database.taskScopeQueries

    override suspend fun initScopes() {
        scopeQueries.transaction {
            val allScope = scopeQueries.selectScope(defaultScope.id).executeAsOneOrNull()
            if (allScope != null) {
                scopeQueries.updateScopeName("Stuff", allScope.scope_id)
                val tasks = taskQueries.selectTasksWithoutScope().executeAsList()
                tasks.forEach {
                    taskScopeQueries.insert(it.id, allScope.scope_id)
                }
            } else {
                scopeQueries.createScope(generateUUID(), "Stuff", 1)
            }
        }
    }

    override suspend fun createScope(name: String): Either<Failure, ScopeDomain> {
        return scopeQueries.transactionWithResult {

            scopeQueries
                .selectScopeByName(name, dataMappers.scopeDbMapper)
                .executeAsOneOrNull()?.let { existingScope: ScopeDomain ->
                    return@transactionWithResult existingScope.right()
                }

            val count = scopeQueries.countScopes().executeAsOne().toInt()
            scopeQueries.createScope(generateUUID(), name, count)
            val scopeId = scopeQueries.lastInsertRowId().executeAsOne();
            scopeQueries
                .selectScope(scopeId, dataMappers.scopeDbMapper)
                .executeAsOne()
                .right()
        }
    }

    override suspend fun deleteScope(id: Long): Either<Failure, ScopeDomain> =
        scopeQueries.transactionWithResult {

            val deleted = scopeQueries.selectScope(
                id = id,
                mapper = dataMappers.scopeDbMapper
            ).executeAsOne()

            scopeQueries.deleteScope(id)

            scopeQueries
                .selectAllScopes()
                .executeAsList()
                .forEachIndexed { index, scope ->
                    scopeQueries.updateScopeOrder(
                        scopeId = scope.scope_id,
                        scopeOrder = index + 1
                    )
                }

            deleted.right()
        }

    override suspend fun getScopes(): Either<Failure, List<ScopeDomain>> = scopeQueries
        .selectAllScopes(mapper = dataMappers.scopeDbMapper)
        .executeAsList()
        .right()

    override fun getScopesFlow(): Flow<List<ScopeDomain>> = scopeQueries
        .selectAllScopes(mapper = dataMappers.scopeDbMapper)
        .asFlow()
        .mapToList(Dispatchers.IO)

    override suspend fun updateScopeName(id: Long, name: String): Either<Failure, ScopeDomain> =
        scopeQueries.transactionWithResult {
            scopeQueries.updateScopeName(name, id)
            scopeQueries
                .selectScope(id, dataMappers.scopeDbMapper)
                .executeAsOne()
                .right()
        }

    // TODO: consider passing the entire list of scopes that will update their order
    override suspend fun updateScopeOrder(id: Long, order: Int): Either<Failure, ScopeDomain> =
        scopeQueries.transactionWithResult {
            scopeQueries.updateScopeOrder(
                scopeId = id,
                scopeOrder = order
            )

            scopeQueries
                .selectAllScopes()
                .executeAsList()
                .map { if (it.scope_id == id) it.copy(scope_order = order) else it }
                .sortedBy { it.scope_order }
                .onEachIndexed { index, scope ->
                    scopeQueries.updateScopeOrder(
                        scopeId = scope.scope_id,
                        scopeOrder = index + 1
                    )
                }


            scopeQueries.selectScope(id, dataMappers.scopeDbMapper)
                .executeAsOne()
                .right()
        }

    override suspend fun updateScopesOrder(scopesOrder: List<Pair<Long, Int>>) {
        scopeQueries.transaction {
            scopesOrder.forEach {
                scopeQueries.updateScopeOrder(
                    scopeId = it.first,
                    scopeOrder = it.second
                )
            }
        }
    }
}