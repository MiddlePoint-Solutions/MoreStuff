package io.middlepoint.morestuff.shared.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

interface UpdateScopeNameUseCase {
    suspend operator fun invoke(scopeId: Long, newName: String)
}

class UpdateScopeNameUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : UpdateScopeNameUseCase {
    override suspend fun invoke(scopeId: Long, newName: String) {
        scopeRepository.updateScopeName(scopeId, newName)
    }
}
