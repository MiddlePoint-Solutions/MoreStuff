package io.middlepoint.morestuff.shared

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.datetime.LocalDateTime

class MediaHandlerImpl : MediaHandler {

    override suspend fun saveMedia(media: KmpFile, time: LocalDateTime): String? {
        TODO("Not yet implemented")
    }

    override fun shareImage(imagePath: String) {
        TODO("Not yet implemented")
    }

    override suspend fun savePDF(media: KmpFile): String? {
        TODO("Not yet implemented")
    }

    override fun sharePDF(pdfPath: String) {
        TODO("Not yet implemented")
    }

    override fun openPDF(pdfPath: String) {
        TODO("Not yet implemented")
    }
}