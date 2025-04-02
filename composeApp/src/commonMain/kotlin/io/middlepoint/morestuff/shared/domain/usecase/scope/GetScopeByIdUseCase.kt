package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface GetScopeByIdUseCase {
    suspend operator fun invoke(id: Long): Either<Failure, ScopeDomain>
}

class GetScopeByIdUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopeByIdUseCase {
    override suspend fun invoke(id: Long): Either<Failure, ScopeDomain> {
        return scopeRepository.getScopeById(id)
    }
}