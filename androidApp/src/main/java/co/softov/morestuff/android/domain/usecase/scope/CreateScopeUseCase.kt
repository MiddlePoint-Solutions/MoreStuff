package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

interface CreateScopeUseCase {
    suspend operator fun invoke(scopeUid: String, name: String)
}

class CreateScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : CreateScopeUseCase {
    override suspend fun invoke(scopeUid: String, name: String) {
        scopeRepository.createScope(scopeUid, name)
    }
}
