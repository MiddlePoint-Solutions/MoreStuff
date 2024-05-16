package io.middlepoint.morestuff.android.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScopeDomain
import io.middlepoint.morestuff.android.domain.repository.ScopeRepository

interface DeleteScopeUseCase {
    suspend operator fun invoke(scopeId: Long): Either<Failure, ScopeDomain>
}

class DeleteScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : DeleteScopeUseCase {
    override suspend fun invoke(scopeId: Long): Either<Failure, ScopeDomain> =
        scopeRepository.deleteScope(scopeId)
}
