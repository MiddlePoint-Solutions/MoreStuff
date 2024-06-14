package io.middlepoint.morestuff.shared.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository

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
