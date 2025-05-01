package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface CreateMessageUseCase {
    suspend operator fun invoke(
      taskId: Uuid,
      title: String,
      contentType: ContentType,
      messageExtra: MessageExtra?,
      scheduleId: Uuid?,
    ): Either<Failure, Message>
}

class CreateMessageUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val checkForUrlMetadataUseCase: CheckForUrlMetadataUseCase
) : CreateMessageUseCase {

    override suspend fun invoke(
      taskId: Uuid,
      title: String,
      contentType: ContentType,
      messageExtra: MessageExtra?,
      scheduleId: Uuid?,
    ): Either<Failure, Message> {
        return messageRepository.createMessage(taskId, scheduleId, contentType.value,messageExtra, title)
            .onRight {
              checkForUrlMetadataUseCase(title, it.id)
            }
    }
}

