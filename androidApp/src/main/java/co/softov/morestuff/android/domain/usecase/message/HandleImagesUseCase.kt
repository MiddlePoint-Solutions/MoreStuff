package co.softov.morestuff.android.domain.usecase.message

import android.net.Uri
import co.softov.morestuff.android.domain.service.ImageHandler

interface HandleImagesUseCase {
    suspend operator fun invoke(uris: Uri, id: Long): String
}

class HandleImagesUseCaseImpl(
    private val imageHandler: ImageHandler
) : HandleImagesUseCase {
    override suspend fun invoke(uris: Uri, id: Long): String {
        return imageHandler.handleImages(uris, id)
    }
}
