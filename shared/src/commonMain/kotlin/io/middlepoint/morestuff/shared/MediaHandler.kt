package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.datetime.LocalDateTime

interface MediaHandler {
    suspend fun saveMedia(media: KmpFile, time: LocalDateTime): String?
    fun shareImage(imagePath: String)

    suspend fun savePDF(media: KmpFile): String?
    fun sharePDF(pdfPath: String)
    fun openPDF(pdfPath: String)
}
