package io.middlepoint.morestuff.shared.domain.repository

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import kotlinx.coroutines.flow.Flow

interface ScopeRepository {
    suspend fun initScopes()
    suspend fun createScope(name: String): Either<Failure, Scope>
    suspend fun deleteScope(id: Long): Either<Failure, Scope>
    suspend fun getScopes(): Either<Failure, List<Scope>>
    fun getScopesFlow(): Flow<List<Scope>>
    suspend fun updateScopeName(id: Long, name: String): Either<Failure, Scope>
    suspend fun updateScopeOrder(id: Long, order: Int): Either<Failure, Scope>
    suspend fun updateScopesOrder(scopesOrder: List<Pair<Long, Int>>)
    suspend fun getScopeByTaskId(taskId: Long): Either<Failure, Scope>
}