package io.middlepoint.morestuff.shared.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface UpdateScopesOrderUseCase {
    suspend operator fun invoke(scopes: List<Scope>)
}

class UpdateScopesOrderUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : UpdateScopesOrderUseCase {
    override suspend fun invoke(scopes: List<Scope>) {
        val scopesToOrder = scopes.mapIndexed { index, scope -> scope.id to index }
        scopeRepository.updateScopesOrder(scopesToOrder)
    }
}
