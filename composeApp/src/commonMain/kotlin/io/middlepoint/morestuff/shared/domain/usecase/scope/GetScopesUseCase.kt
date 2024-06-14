package io.middlepoint.morestuff.shared.domain.usecase.scope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface GetScopesUseCase {
    suspend operator fun invoke(): Either<Failure, List<ScopeDomain>>
}

class GetScopesUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopesUseCase {
    override suspend fun invoke(): Either<Failure, List<ScopeDomain>> = scopeRepository.getScopes()
}
