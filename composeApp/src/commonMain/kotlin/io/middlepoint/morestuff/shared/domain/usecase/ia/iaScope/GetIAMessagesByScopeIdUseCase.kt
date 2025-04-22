package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.repository.ScopeIAMessageRepository
import kotlinx.coroutines.flow.Flow

interface GetIAMessagesByScopeIdUseCase {
    operator fun invoke(scopeId: Long): Flow<List<IAMessage>>
}

class GetIAMessagesByScopeIdUseCaseImpl(
    private val repository: ScopeIAMessageRepository
) : GetIAMessagesByScopeIdUseCase {
    override fun invoke(scopeId: Long): Flow<List<IAMessage>> =
        repository.getIAMessagesByScopeId(scopeId)
}