package io.middlepoint.morestuff.shared.domain.usecase.ia.iaScope

import arrow.core.Either
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.SaveImageFailure
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.message.SaveMediaUseCase

interface CreateScopeIAMediaMessageUseCase {
    suspend operator fun invoke(
        scopeId: Long,
        contentType: ContentType,
        mediaFile: PlatformFile,
        message: String,
    ): Either<Failure, IAMessage>
}

class CreateScopeIAMediaMessageUseCaseImpl(
  private val saveMediaUseCase: SaveMediaUseCase,
  private val createScopeIAMessageUseCase: CreateScopeIAMessageUseCase,
  private val timeManager: TimeManager,
) : CreateScopeIAMediaMessageUseCase {

    override suspend fun invoke(
        scopeId: Long,
        contentType: ContentType,
        mediaFile: PlatformFile,
        message: String,
    ): Either<Failure, IAMessage> {

        return when (val imageResult = saveMediaUseCase(mediaFile)) {
            is Either.Left -> {
                Either.Left(SaveImageFailure(imageResult.value))
            }
            is Either.Right -> {
                val imagePath = imageResult.value
                val creationTime = timeManager.getCreateTime()
                val messageData = MessageData(scopeId, imagePath, creationTime, MessageDataType.Image)

                createScopeIAMessageUseCase.invoke(
                    scopeId = scopeId,
                    content = message,
                    contentType = contentType,
                    messageData = messageData
                )
            }
        }
    }
}