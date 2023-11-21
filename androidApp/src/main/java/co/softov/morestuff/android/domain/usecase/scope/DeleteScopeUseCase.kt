package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.repository.ScopeRepository

interface DeleteScopeUseCase {
    suspend operator fun invoke(scopeIds: List<Long>)
}

class DeleteScopeUseCaseImpl(
    private val scopeRepository: ScopeRepository,
) : DeleteScopeUseCase {
    override suspend fun invoke(scopeIds: List<Long>) {
        scopeRepository.deleteScope(scopeIds)
    }
}
