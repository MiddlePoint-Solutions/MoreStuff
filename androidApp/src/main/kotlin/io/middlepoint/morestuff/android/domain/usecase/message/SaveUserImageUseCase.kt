package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.service.ImageHandler

interface SaveUserImageUseCase {
    suspend operator fun invoke(uris: String): Either<String, String>
}

class SaveUserImageUseCaseImpl(
    private val imageHandler: ImageHandler
) : SaveUserImageUseCase {

    override suspend fun invoke(uris: String): Either<String, String> {
        return try {
            val result = imageHandler.saveImages(uris)
            result?.let { Either.Right(it) } ?: Either.Left("No image path returned")
        } catch (e: Exception) {
            Either.Left("Error in SaveUserImageUseCase")
        }
    }
}
