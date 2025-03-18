package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import com.mohamedrejeb.calf.io.KmpFile
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.SavePdfFailure
import io.middlepoint.morestuff.shared.domain.service.TimeManager

interface CreatePDFMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        contentType: ContentType,
        pdfFile: KmpFile,
        message: String,
    ): Either<Failure, Message>
}
class CreatePDFMessageUseCaseImpl(
    private val saveUserPdfUseCase: SaveUserPDFUseCase,
    private val createMessageUseCase: CreateMessageUseCase,
    private val timeManager: TimeManager,
) : CreatePDFMessageUseCase {

    override suspend fun invoke(
        taskId: Long,
        scheduleId: Long,
        contentType: ContentType,
        pdfFile: KmpFile,
        message: String,
    ): Either<Failure, Message> {

        return when (val pdfResult = saveUserPdfUseCase(pdfFile)) {
            is Either.Left -> Either.Left(SavePdfFailure(pdfResult.toString()))
            is Either.Right -> {
                val pdfPath = pdfResult.value
                val creationTime = timeManager.getCreateTime()
                val messageData = MessageData(taskId, pdfPath, creationTime, MessageDataType.Pdf)

                createMessageUseCase(taskId, message, contentType, messageData, scheduleId)
            }
        }
    }
}
