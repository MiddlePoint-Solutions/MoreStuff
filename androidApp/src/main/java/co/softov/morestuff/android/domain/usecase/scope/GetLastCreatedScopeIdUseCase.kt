package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

interface GetLastCreatedScopeIdUseCase {
    suspend operator fun invoke(): Long?
}

class GetLastCreatedScopeIdUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetLastCreatedScopeIdUseCase {
    override suspend fun invoke(): Long? {
        return scopeRepository.getLastCreatedScopeId()
    }
}

