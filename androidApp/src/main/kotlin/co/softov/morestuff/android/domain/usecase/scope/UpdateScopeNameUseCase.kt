package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

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
