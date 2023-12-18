package co.softov.morestuff.android.domain.usecase.scope

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.repository.ScopeRepository
import kotlinx.coroutines.flow.Flow

interface GetScopesFlowUseCase {
    operator fun invoke(): Flow<List<ScopeDomain>>
}

class GetScopesFlowUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopesFlowUseCase {
    override fun invoke(): Flow<List<ScopeDomain>> = scopeRepository.getScopesFlow()
}
