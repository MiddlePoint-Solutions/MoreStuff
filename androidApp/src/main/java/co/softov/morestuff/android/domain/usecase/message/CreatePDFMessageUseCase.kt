package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.model.SavePdfFailure
import co.softov.morestuff.android.domain.service.TimeManager

interface CreatePDFMessageUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleId: Long = 0,
        contentType: ContentType,
        filePath: String,
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
        filePath: String,
        message: String,
    ): Either<Failure, Message> {

        return when (val pdfResult = saveUserPdfUseCase(filePath)) {
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
