package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.service.ImageHandler

interface SaveUserImageUseCase {
    suspend operator fun invoke(uris: String, id: Long): Either<String, String>
}

class SaveUserImageUseCaseImpl(
    private val imageHandler: ImageHandler
) : SaveUserImageUseCase {

    override suspend fun invoke(uris: String, id: Long): Either<String, String> {
        return try {
            val result = imageHandler.handleImages(uris, id)
            result?.let { Either.Right(it) } ?: Either.Left("No image path returned")
        } catch (e: Exception) {
            Either.Left("Error in SaveUserImageUseCase")
        }
    }
}
