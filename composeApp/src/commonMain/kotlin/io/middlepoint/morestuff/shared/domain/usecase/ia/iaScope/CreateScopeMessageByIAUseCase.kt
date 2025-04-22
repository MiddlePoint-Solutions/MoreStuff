package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.domain.usecase.ia.GenerateChatCompletionUseCase

interface CreateScopeMessageByIAUseCase {
    suspend operator fun invoke(
        scopeId: Long,
        prompt: String
    ): Either<Failure, IAMessage>
}

class CreateScopeMessageByIAUseCaseImpl(
    private val generateChatCompletionUseCase: GenerateChatCompletionUseCase,
    private val createScopeIAMessageUseCase: CreateScopeIAMessageUseCase,
) : CreateScopeMessageByIAUseCase {

    override suspend fun invoke(
        scopeId: Long,
        prompt: String
    ): Either<Failure, IAMessage> {
        val aiResponse = generateChatCompletionUseCase(prompt, scopeId)
        logger.d { "AI response: $aiResponse" }
        return createScopeIAMessageUseCase(
            scopeId = scopeId,
            content = aiResponse,
            contentType = ContentType.AI_TASK_MESSAGE,
            messageData = null
        )
    }
}