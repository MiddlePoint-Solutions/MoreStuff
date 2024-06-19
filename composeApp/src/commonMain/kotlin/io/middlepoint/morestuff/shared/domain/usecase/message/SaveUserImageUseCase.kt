package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.ImageHandler
import io.middlepoint.morestuff.shared.domain.service.TimeManager

interface SaveUserImageUseCase {
    suspend operator fun invoke(uris: String): Either<String, String>
}

class SaveUserImageUseCaseImpl(
    private val imageHandler: ImageHandler,
    private val timeManager: TimeManager,
) : SaveUserImageUseCase {

    override suspend fun invoke(uris: String): Either<String, String> {
        return try {
            val result = imageHandler.saveImages(uris, timeManager.nowLocalDateTime)
            result?.let { Either.Right(it) } ?: Either.Left("No image path returned")
        } catch (e: Exception) {
            Either.Left("Error in SaveUserImageUseCase")
        }
    }
}
