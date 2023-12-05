package co.softov.morestuff.android.domain.usecase.scope

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.repository.ScopeRepository

interface DeleteScopeUseCase {
    suspend operator fun invoke(scopeId: Long): Either<Failure, ScopeDomain>
}

class DeleteScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : DeleteScopeUseCase {
    override suspend fun invoke(scopeId: Long): Either<Failure, ScopeDomain> =
        scopeRepository.deleteScope(scopeId)
}
