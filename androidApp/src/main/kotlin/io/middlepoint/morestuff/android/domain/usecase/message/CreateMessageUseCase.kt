package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.model.MessageData
import io.middlepoint.morestuff.android.domain.repository.MessageRepository

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
        return messageRepository.createMessage(taskId, scheduleId, contentType.value,messageData, title).tap {
            checkForUrlMetadataUseCase(title, it.id)
        }
    }
}

