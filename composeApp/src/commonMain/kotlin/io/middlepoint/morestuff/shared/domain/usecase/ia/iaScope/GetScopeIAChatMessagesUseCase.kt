package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.repository.ScopeIAMessageRepository
import kotlinx.coroutines.flow.Flow

interface GetScopeIAChatMessagesUseCase {
    operator fun invoke(scopeId: Long): GetScopeIAChatMessagesUseCase
    fun asList(): List<IAMessage>
    fun asFlow(): Flow<List<IAMessage>>
}

class GetScopeIAChatMessagesUseCaseImpl(
    private val repository: ScopeIAMessageRepository
) : GetScopeIAChatMessagesUseCase {

    private var scopeId: Long = 0

    override fun invoke(scopeId: Long): GetScopeIAChatMessagesUseCase {
        this.scopeId = scopeId
        return this
    }

    override fun asList(): List<IAMessage> = repository.getScopeIAChatMessages(scopeId)

    override fun asFlow(): Flow<List<IAMessage>> = repository.getScopeIAChatMessagesFlow(scopeId)
}