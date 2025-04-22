package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import io.middlepoint.morestuff.shared.domain.repository.ScopeIAMessageRepository

interface DeleteScopeIAMessageUseCase {
    suspend operator fun invoke(messageId: Long)
}

class DeleteScopeIAMessageUseCaseImpl(
    private val repository: ScopeIAMessageRepository
) : DeleteScopeIAMessageUseCase {
    override suspend fun invoke(messageId: Long) =
        repository.deleteScopeIAMessage(messageId)
}