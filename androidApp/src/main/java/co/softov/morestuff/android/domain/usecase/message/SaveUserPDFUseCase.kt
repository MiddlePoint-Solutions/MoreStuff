package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.SavePdfFailure
import co.softov.morestuff.android.domain.service.PDFHandler

/*interface SaveUserPDFUseCase {
    suspend operator fun invoke(uri: String): Either<String, String>
}
class SaveUserPDFUseCaseImpl(
    private val pdfHandler: PDFHandler
) : SaveUserPDFUseCase {

    override suspend fun invoke(uri: String): Either<String, String> {
        return try {
            val result = pdfHandler.savePDF(uri)
            result?.let { Either.Right(it) } ?: Either.Left("No PDF path returned")
        } catch (e: Exception) {
            Either.Left("Error in SaveUserPdfUseCase")
        }
    }
}*/

interface SaveUserPDFUseCase {
    suspend operator fun invoke(uri: String): Either<Failure, String>
}

class SaveUserPDFUseCaseImpl(
    private val pdfHandler: PDFHandler
) : SaveUserPDFUseCase {

    override suspend fun invoke(uri: String): Either<Failure, String> {
        return try {
            val result = pdfHandler.savePDF(uri)
            result?.let { Either.Right(it) } ?: Either.Left(SavePdfFailure("No PDF path returned"))
        } catch (e: Exception) {
            Either.Left(SavePdfFailure("Error in SaveUserPdfUseCase: ${e.message}"))
        }
    }
}