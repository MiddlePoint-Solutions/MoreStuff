package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.service.PdfHandler

interface SaveUserPdfUseCase {
    suspend operator fun invoke(uri: String): Either<String, String>
}
class SaveUserPdfUseCaseImpl(
    private val pdfHandler: PdfHandler
) : SaveUserPdfUseCase {

    override suspend fun invoke(uri: String): Either<String, String> {
        return try {
            val result = pdfHandler.savePdf(uri)
            result?.let { Either.Right(it) } ?: Either.Left("No PDF path returned")
        } catch (e: Exception) {
            Either.Left("Error in SaveUserPdfUseCase")
        }
    }
}
