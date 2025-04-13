package io.middlepoint.morestuff.shared.domain.usecase.scope

import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.repository.ScopeRepository
import kotlinx.coroutines.flow.Flow

interface GetScopesFlowUseCase {
    operator fun invoke(): Flow<List<ScopeDomain>>
}

class GetScopesFlowUseCaseImpl(
    private val scopeRepository: ScopeRepository
) : GetScopesFlowUseCase {
    override fun invoke(): Flow<List<ScopeDomain>> = scopeRepository.getScopesFlow()
}
