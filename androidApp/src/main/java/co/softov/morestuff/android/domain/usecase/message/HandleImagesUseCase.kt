package co.softov.morestuff.android.domain.usecase.message

import android.net.Uri
import co.softov.morestuff.android.domain.service.ImageHandlerInterface

interface HandleImagesUseCase {
    suspend operator fun invoke(uris: Uri, id: Long): String
}

class HandleImagesUseCaseImpl(
    private val imageHandlerInterface: ImageHandlerInterface
) : HandleImagesUseCase {
    override suspend fun invoke(uris: Uri, id: Long): String {
        return imageHandlerInterface.handleImages(uris, id)
    }
}
