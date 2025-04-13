package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface CreateScopeUseCase {
    suspend operator fun invoke(name: String): Either<Failure, ScopeDomain>

}

class CreateScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : CreateScopeUseCase {
    override suspend fun invoke(name: String): Either<Failure, ScopeDomain> =
        scopeRepository.createScope(name)

}
