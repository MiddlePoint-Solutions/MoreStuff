package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageWithData
import co.softov.morestuff.android.domain.repository.MessageRepository

interface CreateMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        contentType: ContentType,
        messageWithData: MessageWithData?,
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
        messageWithData: MessageWithData?,
        scheduleId: Long,
    ): Either<Failure, Message> {
        val messageResult = messageRepository.createMessage(taskId, scheduleId, contentType.value,messageWithData, title)
        messageResult.map { message ->
            checkForUrlMetadataUseCase(title, message.id)
        }
        return messageResult
    }
}

