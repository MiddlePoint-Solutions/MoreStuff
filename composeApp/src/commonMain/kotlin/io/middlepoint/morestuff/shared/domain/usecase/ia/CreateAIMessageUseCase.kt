package io.middlepoint.morestuff.shared.domain.usecase.ia

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.AIUnexpectedFailure
import io.middlepoint.morestuff.shared.domain.model.ApiKeyNotFound
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.domain.usecase.message.CreateMessageUseCase


interface CreateAIMessageUseCase {
    suspend operator fun invoke(
        taskId: Uuid,
        prompt: String
    ): Either<Failure, Message>
}

class CreateAIMessageUseCaseImpl(
  private val generateChatCompletionUseCase: GenerateChatCompletionUseCase,
  private val createMessageUseCase: CreateMessageUseCase,
) : CreateAIMessageUseCase {

  override suspend fun invoke(
    taskId: Uuid,
    prompt: String
  ): Either<Failure, Message> {
    return try {
      val aiResponse = generateChatCompletionUseCase(prompt, taskId)
      logger.d { "AI response: $aiResponse" }
      createMessageUseCase(
        taskId = taskId,
        contentType = ContentType.AI_TASK_MESSAGE,
        messageExtra = null,
        title = aiResponse,
        scheduleId = null
      )
    } catch (e: IllegalStateException) {
      if (e.message?.contains("API key not found") == true) {
        Either.Left(ApiKeyNotFound)
      } else {
        Either.Left(AIUnexpectedFailure(e.message))
      }
    } catch (e: Exception) {
      Either.Left(AIUnexpectedFailure(e.message))
    }
  }
}