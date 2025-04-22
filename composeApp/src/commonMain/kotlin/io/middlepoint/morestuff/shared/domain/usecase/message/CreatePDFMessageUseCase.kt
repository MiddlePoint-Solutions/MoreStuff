package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.SavePdfFailure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.TimeManager

interface CreatePDFMessageUseCase {
    suspend operator fun invoke(
        taskId: Uuid,
        scheduleId: Uuid?,
        contentType: ContentType,
        pdfFile: PlatformFile,
        message: String,
    ): Either<Failure, Message>
}
class CreatePDFMessageUseCaseImpl(
    private val saveUserPdfUseCase: SaveUserPDFUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val timeManager: TimeManager,
) : CreatePDFMessageUseCase {

    override suspend fun invoke(
        taskId: Uuid,
        scheduleId: Uuid?,
        contentType: ContentType,
        pdfFile: PlatformFile,
        message: String,
    ): Either<Failure, Message> {

        return when (val pdfResult = saveUserPdfUseCase(pdfFile)) {
            is Either.Left -> Either.Left(SavePdfFailure(pdfResult.toString()))
            is Either.Right -> {
                val pdfPath = pdfResult.value
                val creationTime = timeManager.getCreatedTime()
                val messageExtra = MessageExtra(taskId, pdfPath, creationTime, MessageExtraType.Pdf)

                createMessageUseCase(taskId, message, contentType, messageExtra, scheduleId)
            }
        }
    }
}
