package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.right
import io.middlepoint.morestuff.shared.generateUUID
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.NoScope
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.model.core.defaultScope
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

    override suspend fun createScope(name: String): Either<Failure, Scope> {
        return scopeQueries.transactionWithResult {

            scopeQueries
                .selectScopeByName(name, dataMappers.scopeDataMapper)
                .executeAsOneOrNull()?.let { existingScope: Scope ->
                    return@transactionWithResult existingScope.right()
                }

            val count = scopeQueries.countScopes().executeAsOne().toInt()
            scopeQueries.createScope(generateUUID(), name, count)
            val scopeId = scopeQueries.lastInsertRowId().executeAsOne();
            scopeQueries
                .selectScope(scopeId, dataMappers.scopeDataMapper)
                .executeAsOne()
                .right()
        }
    }

    override suspend fun deleteScope(id: Long): Either<Failure, Scope> =
        scopeQueries.transactionWithResult {

            val deleted = scopeQueries.selectScope(
                id = id,
                mapper = dataMappers.scopeDataMapper
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

    override suspend fun getScopes(): Either<Failure, List<Scope>> = scopeQueries
        .selectAllScopes(mapper = dataMappers.scopeDataMapper)
        .executeAsList()
        .right()

    override fun getScopesFlow(): Flow<List<Scope>> = scopeQueries
        .selectAllScopes(mapper = dataMappers.scopeDataMapper)
        .asFlow()
        .mapToList(Dispatchers.IO)

    override suspend fun updateScopeName(id: Long, name: String): Either<Failure, Scope> =
        scopeQueries.transactionWithResult {
            scopeQueries.updateScopeName(name, id)
            scopeQueries
                .selectScope(id, dataMappers.scopeDataMapper)
                .executeAsOne()
                .right()
        }

    // TODO: consider passing the entire list of scopes that will update their order
    override suspend fun updateScopeOrder(id: Long, order: Int): Either<Failure, Scope> =
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


            scopeQueries.selectScope(id, dataMappers.scopeDataMapper)
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

    override suspend fun getScopeByTaskId(taskId: Long): Either<Failure, Scope> {
        val scopeId = taskScopeQueries
            .selectScopeIdForTask(taskId)
            .executeAsOneOrNull() ?: return Either.Left(NoScope)
        return scopeQueries
            .selectScope(scopeId, dataMappers.scopeDataMapper)
            .executeAsOneOrNull()
            ?.right() ?: Either.Left(NoScope)
    }
}