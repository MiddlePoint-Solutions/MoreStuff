package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.repository.ScopeIAMessageRepository
import io.middlepoint.morestuff.shared.domain.usecase.message.CheckForUrlMetadataUseCase

interface CreateScopeIAMessageUseCase {
    suspend operator fun invoke(
        scopeId: Long,
        content: String,
        contentType: ContentType,
        messageData: MessageData? = null
    ): Either<Failure, IAMessage>
}

class CreateScopeIAMessageUseCaseImpl(
    private val repository: ScopeIAMessageRepository,
    private val checkForUrlMetadataUseCase: CheckForUrlMetadataUseCase
) : CreateScopeIAMessageUseCase {
    override suspend fun invoke(
        scopeId: Long,
        content: String,
        contentType: ContentType,
        messageData: MessageData?
    ): Either<Failure, IAMessage> =
        repository.createScopeIAMessage(
            scopeId = scopeId,
            contentType = contentType.value,
            messageData = messageData,
            content = content
        )/*.onRight {
            checkForUrlMetadataUseCase(content, it.id)
        }*/
}