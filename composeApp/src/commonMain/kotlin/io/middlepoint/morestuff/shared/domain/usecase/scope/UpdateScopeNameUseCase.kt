package io.middlepoint.morestuff.shared.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface UpdateScopeNameUseCase {
    suspend operator fun invoke(scopeId: Uuid, newName: String)
}

class UpdateScopeNameUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : UpdateScopeNameUseCase {
    override suspend fun invoke(scopeId: Uuid, newName: String) {
        scopeRepository.updateScopeName(scopeId, newName)
    }
}
