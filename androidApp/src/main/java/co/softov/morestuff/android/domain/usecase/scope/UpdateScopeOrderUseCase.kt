package co.softov.morestuff.android.domain.usecase.scope

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.repository.ScopeRepository

interface UpdateScopeOrderUseCase {
    suspend operator fun invoke(scopeId: Long, order: Int): Either<Failure, ScopeDomain>
}
class UpdateScopeOrderUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : UpdateScopeOrderUseCase {
    override suspend fun invoke(scopeId: Long, order: Int): Either<Failure, ScopeDomain> =
        scopeRepository.updateScopeOrder(scopeId, order)
}
