package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

interface UpdateScopeOrderUseCase {
    suspend operator fun invoke(scopeId: Long, newOrderIndex: Long)
}
class UpdateScopeOrderUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : UpdateScopeOrderUseCase {
    override suspend fun invoke(scopeId: Long, newOrderIndex: Long) {
        scopeRepository.updateScopeOrder(scopeId, newOrderIndex)
    }
}
