package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.MediaHandler
import io.middlepoint.morestuff.shared.domain.service.TimeManager

interface SaveMediaUseCase {
    suspend operator fun invoke(media: PlatformFile): Either<String, String>
}

class SaveMediaUseCaseImpl(
  private val mediaHandler: MediaHandler,
  private val timeManager: TimeManager,
) : SaveMediaUseCase {

    override suspend fun invoke(media: PlatformFile): Either<String, String> {
        return try {
            val result = mediaHandler.saveMedia(media, timeManager.nowLocalDateTime)
            result?.let { Either.Right(it) } ?: Either.Left("No image path returned")
        } catch (e: Exception) {
            Either.Left("Error in SaveUserImageUseCase")
        }
    }
}
