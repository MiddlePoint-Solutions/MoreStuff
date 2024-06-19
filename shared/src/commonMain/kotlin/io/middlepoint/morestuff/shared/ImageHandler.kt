package io.middlepoint.morestuff.shared

import kotlinx.datetime.LocalDateTime

interface ImageHandler {
    suspend fun saveImages(uris: String, time: LocalDateTime): String?
    fun shareImage(imagePath: String)
}
