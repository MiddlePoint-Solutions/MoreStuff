package io.middlepoint.morestuff.android.domain.service

interface ImageHandler {
    suspend fun saveImages(uris: String): String?
    fun shareImage(imagePath: String)
}
