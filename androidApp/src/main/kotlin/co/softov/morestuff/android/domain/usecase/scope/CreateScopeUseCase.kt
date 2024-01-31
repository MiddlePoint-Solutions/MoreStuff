package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

interface CreateScopeUseCase {
    suspend operator fun invoke(name: String)
}

class CreateScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : CreateScopeUseCase {
    override suspend fun invoke(name: String) {
        scopeRepository.createScope(name)
    }
}
