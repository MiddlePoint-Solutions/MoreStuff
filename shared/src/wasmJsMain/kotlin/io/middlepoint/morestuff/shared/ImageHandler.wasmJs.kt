package io.middlepoint.morestuff.shared

import kotlinx.datetime.LocalDateTime

actual class ImageHandlerImpl : ImageHandler {
    override suspend fun saveImages(uris: String, time: LocalDateTime): String? {
        TODO("Not yet implemented")
    }

    override fun shareImage(imagePath: String) {
        TODO("Not yet implemented")
    }
}