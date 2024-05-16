package io.middlepoint.morestuff.android.domain.usecase.scope

import io.middlepoint.morestuff.android.domain.model.ScopeDomain
import io.middlepoint.morestuff.android.domain.repository.ScopeRepository

interface UpdateScopesOrderUseCase {
    suspend operator fun invoke(scopes: List<ScopeDomain>)
}

class UpdateScopesOrderUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : UpdateScopesOrderUseCase {
    override suspend fun invoke(scopes: List<ScopeDomain>) {
        val scopesToOrder = scopes.mapIndexed { index, scope -> scope.id to index }
        scopeRepository.updateScopesOrder(scopesToOrder)
    }
}
