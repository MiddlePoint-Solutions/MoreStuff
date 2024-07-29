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
}