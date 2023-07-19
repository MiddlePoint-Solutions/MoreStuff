package co.softov.morestuff.android.domain.usecase.message

import android.net.Uri
import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.service.TimeManager


interface CreateImageMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        contentType: ContentType,
        filePath: Uri,
        message: String,
    ): Either<Failure, Message>
}

class CreateImageMessageUseCaseImpl(
    private val handleImagesUseCase: HandleImagesUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val timeManager: TimeManager,
) : CreateImageMessageUseCase {
    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        contentType: ContentType,
        filePath: Uri,
        message: String,
    ): Either<Failure, Message> {
        val imagePath = handleImagesUseCase(filePath, taskId)
        val creationTime = timeManager.getCreateTime()
        val messageData = MessageData(taskId, imagePath, creationTime, MessageDataType.Image)

        return createMessageUseCase.invoke(taskId, message, contentType, messageData, scheduleId)
    }
}

