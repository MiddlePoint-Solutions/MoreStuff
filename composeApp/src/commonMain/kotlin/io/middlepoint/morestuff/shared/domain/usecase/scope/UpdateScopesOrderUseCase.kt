package io.middlepoint.morestuff.shared.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

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
