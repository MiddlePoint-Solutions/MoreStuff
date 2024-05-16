package io.middlepoint.morestuff.android.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScopeDomain
import io.middlepoint.morestuff.android.domain.repository.ScopeRepository

interface CreateScopeUseCase {
    suspend operator fun invoke(name: String): Either<Failure, ScopeDomain>

}

class CreateScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : CreateScopeUseCase {
    override suspend fun invoke(name: String): Either<Failure, ScopeDomain> =
        scopeRepository.createScope(name)

}
