package io.middlepoint.morestuff.shared.domain.service

interface ImageHandler {
    suspend fun saveImages(uris: String): String?
    fun shareImage(imagePath: String)
}
