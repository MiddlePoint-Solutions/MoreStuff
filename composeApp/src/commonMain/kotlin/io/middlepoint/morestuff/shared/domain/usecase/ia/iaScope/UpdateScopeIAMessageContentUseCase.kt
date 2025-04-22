package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.ScopeIAMessageRepository

interface UpdateScopeIAMessageContentUseCase {
    suspend operator fun invoke(
        messageId: Long,
        content: String
    ): Either<Failure, Boolean>
}

class UpdateScopeIAMessageContentUseCaseImpl(
    private val repository: ScopeIAMessageRepository
) : UpdateScopeIAMessageContentUseCase {
    override suspend fun invoke(
        messageId: Long,
        content: String
    ): Either<Failure, Boolean> =
        repository.updateScopeIAMessageContent(messageId, content)
}