package co.softov.morestuff.android.domain.usecase.message

import android.content.Context
import android.net.Uri
import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository

interface CreateMessageWithImageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        contentType: ContentType,
        scheduleId: Long = 0,
        uris: Uri,
        context: Context
    ): Either<Failure, Message>
}

class CreateMessageWithImageUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val handleImagesUseCase: HandleImagesUseCase
) : CreateMessageWithImageUseCase {

    override suspend fun invoke(
        taskId: Long,
        title: String,
        contentType: ContentType,
        scheduleId: Long,
        uris: Uri,
        context: Context
    ): Either<Failure, Message> {
        val messageResult = messageRepository.createMessage(taskId, scheduleId, contentType.value, title)

        messageResult.map { message ->
            handleImagesUseCase(uris, context, message.id)
        }
        return messageResult
    }
}
