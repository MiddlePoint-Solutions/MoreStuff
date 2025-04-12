package io.middlepoint.morestuff.shared

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.LocalDateTime

class MediaHandlerImpl : MediaHandler {

    override suspend fun saveMedia(media: PlatformFile, time: LocalDateTime): String? {
        TODO("Not yet implemented")
    }

    override fun shareImage(imagePath: String) {
        TODO("Not yet implemented")
    }

    override suspend fun savePDF(media: PlatformFile): String? {
        TODO("Not yet implemented")
    }

    override fun sharePDF(path: String) {
        TODO("Not yet implemented")
    }

    override fun openPDF(path: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveJsonToFile(jsonString: String, fileName: String): String? {
        TODO("Not yet implemented")
    }

    override fun shareFile(path: String) {
        TODO("Not yet implemented")
    }
}