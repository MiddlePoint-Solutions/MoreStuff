package co.softov.morestuff.android.domain.usecase.scope

import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.repository.ScopeRepository
import timber.log.Timber

interface GetScopesUseCase {
    suspend operator fun invoke(): List<ScopeDomain>
}
class GetScopesUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopesUseCase {
    override suspend fun invoke(): List<ScopeDomain> {
        val scopes = scopeRepository.getScopes()
        Timber.d("Loaded scopes: $scopes")
        return scopes
    }
}
