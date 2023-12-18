package co.softov.morestuff.android.domain.usecase.scope

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.repository.ScopeRepository

interface GetScopesUseCase {
    suspend operator fun invoke(): Either<Failure, List<ScopeDomain>>
}

class GetScopesUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopesUseCase {
    override suspend fun invoke(): Either<Failure, List<ScopeDomain>> = scopeRepository.getScopes()
}
