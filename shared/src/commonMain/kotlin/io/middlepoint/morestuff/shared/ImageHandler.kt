package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.datetime.LocalDateTime

interface ImageHandler {
    suspend fun saveMedia(media: KmpFile, time: LocalDateTime): String?
    fun shareImage(imagePath: String)
}
