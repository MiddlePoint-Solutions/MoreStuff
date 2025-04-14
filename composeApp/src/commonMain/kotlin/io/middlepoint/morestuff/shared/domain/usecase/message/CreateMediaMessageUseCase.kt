package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.SaveImageFailure
import io.middlepoint.morestuff.shared.domain.service.TimeManager


interface CreateMediaMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        contentType: ContentType,
        mediaFile: PlatformFile,
        message: String,
    ): Either<Failure, Message>
}

class CreateMediaMessageUseCaseImpl(
    private val saveMediaUseCase: SaveMediaUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val timeManager: TimeManager,
) : CreateMediaMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        contentType: ContentType,
        mediaFile: PlatformFile,
        message: String,
    ): Either<Failure, Message> {

        return when (val imageResult = saveMediaUseCase(mediaFile)) {
            is Either.Left -> {
                Either.Left(SaveImageFailure(imageResult.value))
            }
            is Either.Right -> {
                val imagePath = imageResult.value
                val creationTime = timeManager.getCreatedTime()
                val messageData = MessageData(taskId, imagePath, creationTime, MessageDataType.Image)

                createMessageUseCase.invoke(taskId, message, contentType, messageData, scheduleId)
            }
        }
    }
}
