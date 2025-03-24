package io.middlepoint.morestuff.shared

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.LocalDateTime

interface MediaHandler {
    suspend fun saveMedia(media: PlatformFile, time: LocalDateTime): String?
    fun shareImage(imagePath: String)

    suspend fun savePDF(media: PlatformFile): String?
    fun sharePDF(path: String)
    fun openPDF(path: String)
}
