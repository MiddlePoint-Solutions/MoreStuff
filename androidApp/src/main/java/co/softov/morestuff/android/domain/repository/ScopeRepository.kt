package co.softov.morestuff.android.domain.repository

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScopeDomain
import kotlinx.coroutines.flow.Flow

interface ScopeRepository {
    suspend fun createScope(uid: String, name: String): Either<Failure, ScopeDomain?>
    suspend fun deleteScope(id: Long): Either<Failure, ScopeDomain>
    suspend fun getScopes(): Either<Failure, List<ScopeDomain>>
    fun getScopesFlow(): Flow<List<ScopeDomain>>
    suspend fun updateScopeName(id: Long, name: String): Either<Failure, ScopeDomain>
    suspend fun updateScopeOrder(id: Long, order: Int): Either<Failure, ScopeDomain>
    suspend fun getLastCreatedScopeId(): Long?
}