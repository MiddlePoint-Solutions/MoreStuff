package io.middlepoint.morestuff.shared.domain.usecase.ia

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase

interface CreateAIMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        prompt: String
    ): Either<Failure, Message>
}

class CreateAIMessageUseCaseImpl(
    private val generateChatCompletionUseCase: GenerateChatCompletionUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
) : CreateAIMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        prompt: String
    ): Either<Failure, Message> {
        val aiResponse = generateChatCompletionUseCase(prompt, taskId)
    logger.d { "AI response: $aiResponse" }
        return createMessageUseCase(
          taskId = taskId,
          contentType = ContentType.AI_TASK_MESSAGE,
          messageData = null,
          title = aiResponse,
          scheduleId = 0
        )
    }
}