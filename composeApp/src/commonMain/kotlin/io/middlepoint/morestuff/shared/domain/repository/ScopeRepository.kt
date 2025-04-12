package io.middlepoint.morestuff.shared.domain.repository

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import kotlinx.coroutines.flow.Flow

interface ScopeRepository {
    suspend fun initScopes()
    suspend fun createScope(name: String): Either<Failure, ScopeDomain>
    suspend fun deleteScope(id: Long): Either<Failure, ScopeDomain>
    suspend fun getScopes(): Either<Failure, List<ScopeDomain>>
    fun getScopesFlow(): Flow<List<ScopeDomain>>
    suspend fun updateScopeName(id: Long, name: String): Either<Failure, ScopeDomain>
    suspend fun updateScopeOrder(id: Long, order: Int): Either<Failure, ScopeDomain>
    suspend fun updateScopesOrder(scopesOrder: List<Pair<Long, Int>>)
    suspend fun getScopeByTaskId(taskId: Long): Either<Failure, ScopeDomain>
}