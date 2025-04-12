package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface DeleteScopeUseCase {
    suspend operator fun invoke(scopeId: Long): Either<Failure, ScopeDomain>
}

class DeleteScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : DeleteScopeUseCase {
    override suspend fun invoke(scopeId: Long): Either<Failure, ScopeDomain> =
        scopeRepository.deleteScope(scopeId)
}
