package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.enums.MessageDataType
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.model.MessageData
import io.middlepoint.morestuff.android.domain.model.SaveImageFailure
import io.middlepoint.morestuff.android.domain.service.TimeManager


interface CreateImageMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        contentType: ContentType,
        filePath: String,
        message: String,
    ): Either<Failure, Message>
}

class CreateImageMessageUseCaseImpl(
    private val saveUserImageUseCase: SaveUserImageUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val timeManager: TimeManager,
) : CreateImageMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        contentType: ContentType,
        filePath: String,
        message: String,
    ): Either<Failure, Message> {

        return when (val imageResult = saveUserImageUseCase(filePath)) {
            is Either.Left -> {
                Either.Left(SaveImageFailure(imageResult.value))
            }
            is Either.Right -> {
                val imagePath = imageResult.value
                val creationTime = timeManager.getCreateTime()
                val messageData = MessageData(taskId, imagePath, creationTime, MessageDataType.Image)

                createMessageUseCase.invoke(taskId, message, contentType, messageData, scheduleId)
            }
        }
    }
}
