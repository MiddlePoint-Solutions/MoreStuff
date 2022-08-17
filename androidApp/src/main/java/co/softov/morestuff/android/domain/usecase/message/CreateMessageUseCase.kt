package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository

interface CreateMessageUseCase {

    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        title: String,
        contentType: ContentType
    ): Either<Failure, Message>

}

class CreateMessageUseCaseImpl(
    private val messageRepository: MessageRepository
) : CreateMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        title: String,
        contentType: ContentType
    ): Either<Failure, Message> {
        return messageRepository.createMessage(taskId, scheduleId, contentType.value, title)
    }

}