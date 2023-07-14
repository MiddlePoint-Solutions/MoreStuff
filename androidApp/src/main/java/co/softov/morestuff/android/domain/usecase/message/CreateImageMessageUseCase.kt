package co.softov.morestuff.android.domain.usecase.message

import android.net.Uri
import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import timber.log.Timber


interface CreateImageMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        contentType: ContentType,
        filePath: Uri,
    ): Either<Failure, Message>
}

class CreateImageMessageUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val handleImagesUseCase: HandleImagesUseCase,
) : CreateImageMessageUseCase {
    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        contentType: ContentType,
        filePath: Uri,
    ): Either<Failure, Message> {
        val messageWithData = handleImagesUseCase(filePath, taskId)
        Timber.d("CreateImageMessageUseCaseImpl", "Resulting MessageWithData: $messageWithData")
        return messageRepository.createMessage(taskId, scheduleId, contentType.value, content = "",  messageWithData = messageWithData)
    }
}

