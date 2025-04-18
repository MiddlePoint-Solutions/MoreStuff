package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.model.ScopeData
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.NoScope
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.DEFAULT_SCOPE_NAME
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ScopeRepositoryImpl(
  database: StuffDb,
  private val dataMappers: DataMappers,
  private val timeManager: TimeManager,
) : ScopeRepository {

  private val scopeQueries = database.scopesQueries
  private val taskQueries = database.tasksQueries
  private val taskScopeQueries = database.tasksScopesQueries

  override suspend fun initScopes() {
    scopeQueries.transaction {
      scopeQueries.selectScopeByName(DEFAULT_SCOPE_NAME).executeAsOneOrNull().let {
        if (it == null) {
          val stuffScope = createScopeData(DEFAULT_SCOPE_NAME, 0)
          scopeQueries.createScope(stuffScope)
        }
      }
    }
  }

  private fun createScopeData(name: String, order: Int): ScopeData {
    val createdAt = timeManager.nowUtcInstant
    val stuffScope = ScopeData(
      id = Uuid.generate(),
      scope_name = name,
      scope_order = order,
      created_at = createdAt,
      updated_at = createdAt
    )
    return stuffScope
  }

  override suspend fun createScope(name: String): Either<Failure, Scope> = either {
    scopeQueries.transactionWithResult {
      val count = scopeQueries.countScopes().executeAsOne().toInt()
      createScopeData(name, count)
      scopeQueries
        .selectScopeByName(name, dataMappers.scopeDataMapper)
        .executeAsOne()
    }
  }

  override suspend fun deleteScope(id: Uuid): Either<Failure, Scope> =
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
            scopeId = scope.id,
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

  override suspend fun updateScopeName(id: Uuid, name: String): Either<Failure, Scope> =
    scopeQueries.transactionWithResult {
      scopeQueries.updateScopeName(name, id)
      scopeQueries
        .selectScope(id, dataMappers.scopeDataMapper)
        .executeAsOne()
        .right()
    }

  // TODO: consider passing the entire list of scopes that will update their order
  override suspend fun updateScopeOrder(id: Uuid, order: Int): Either<Failure, Scope> =
    scopeQueries.transactionWithResult {
      scopeQueries.updateScopeOrder(
        scopeId = id,
        scopeOrder = order
      )

      scopeQueries
        .selectAllScopes()
        .executeAsList()
        .map { if (it.id == id) it.copy(scope_order = order) else it }
        .sortedBy { it.scope_order }
        .onEachIndexed { index, scope ->
          scopeQueries.updateScopeOrder(
            scopeId = scope.id,
            scopeOrder = index + 1
          )
        }


      scopeQueries.selectScope(id, dataMappers.scopeDataMapper)
        .executeAsOne()
        .right()
    }

  override suspend fun updateScopesOrder(scopesOrder: List<Pair<Uuid, Int>>) {
    scopeQueries.transaction {
      scopesOrder.forEach {
        scopeQueries.updateScopeOrder(
          scopeId = it.first,
          scopeOrder = it.second
        )
      }
    }
  }

  override suspend fun getScopeByTaskId(taskId: Uuid): Either<Failure, Scope> {
    val scopeId = taskScopeQueries
      .selectScopeIdForTask(taskId)
      .executeAsOneOrNull() ?: return Either.Left(NoScope)
    return scopeQueries
      .selectScope(scopeId, dataMappers.scopeDataMapper)
      .executeAsOneOrNull()
      ?.right() ?: Either.Left(NoScope)
  }
}