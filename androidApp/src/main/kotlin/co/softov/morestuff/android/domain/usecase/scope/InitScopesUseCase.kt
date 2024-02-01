package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

interface InitScopesUseCase {
    suspend operator fun invoke()
}

class InitScopesUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : InitScopesUseCase {
    override suspend fun invoke() {
        scopeRepository.initScopes()
    }
}
