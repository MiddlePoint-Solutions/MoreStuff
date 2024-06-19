package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface CreateMessageUseCase {
    suspend operator fun invoke(
      taskId: Long,
      title: String,
      contentType: ContentType,
      messageData: MessageData?,
      scheduleId: Long = 0,
    ): Either<Failure, Message>
}

class CreateMessageUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val checkForUrlMetadataUseCase: CheckForUrlMetadataUseCase
) : CreateMessageUseCase {

    override suspend fun invoke(
      taskId: Long,
      title: String,
      contentType: ContentType,
      messageData: MessageData?,
      scheduleId: Long,
    ): Either<Failure, Message> {
        return messageRepository.createMessage(taskId, scheduleId, contentType.value,messageData, title)
            .onRight {
                checkForUrlMetadataUseCase(title, it.id)
            }
    }
}

