package io.middlepoint.morestuff.shared.domain.repository

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import kotlinx.coroutines.flow.Flow

interface ScopeRepository {
    suspend fun createScope(name: String): Either<Failure, Scope>
    suspend fun deleteScope(id: Uuid): Either<Failure, Scope>
    suspend fun getScopes(): Either<Failure, List<Scope>>
    fun getScopesFlow(): Flow<List<Scope>>
    suspend fun updateScopeName(id: Uuid, name: String): Either<Failure, Scope>
    suspend fun updateScopeOrder(id: Uuid, order: Int): Either<Failure, Scope>
    suspend fun updateScopesOrder(scopesOrder: List<Pair<Uuid, Int>>)
    suspend fun getScopeByTaskId(taskId: Uuid): Either<Failure, Scope>
}