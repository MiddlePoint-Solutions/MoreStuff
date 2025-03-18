package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import com.mohamedrejeb.calf.io.KmpFile
import io.middlepoint.morestuff.shared.MediaHandler
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.SavePdfFailure

interface SaveUserPDFUseCase {
    suspend operator fun invoke(pdfFile: KmpFile): Either<Failure, String>
}

class SaveUserPDFUseCaseImpl(
    private val mediaHandler: MediaHandler
) : SaveUserPDFUseCase {

    override suspend fun invoke(pdfFile: KmpFile): Either<Failure, String> {
        return try {
            val result = mediaHandler.savePDF(pdfFile)
            result?.let { Either.Right(it) } ?: Either.Left(SavePdfFailure("No PDF path returned"))
        } catch (e: Exception) {
            Either.Left(SavePdfFailure("Error in SaveUserPdfUseCase: ${e.message}"))
        }
    }
}