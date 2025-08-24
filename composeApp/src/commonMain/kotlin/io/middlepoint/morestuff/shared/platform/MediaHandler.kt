package io.middlepoint.morestuff.shared.platform

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.LocalDateTime

interface MediaHandler {
    suspend fun saveMedia(media: PlatformFile, time: LocalDateTime): String?
    fun shareImage(imagePath: String)

    suspend fun savePDF(media: PlatformFile): String?
    fun sharePDF(path: String)
    fun openPDF(path: String)

    suspend fun saveJsonToFile(jsonString: String, fileName: String): String?
    fun shareFile(path: String)
}
