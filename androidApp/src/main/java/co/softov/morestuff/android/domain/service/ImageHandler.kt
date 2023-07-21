package co.softov.morestuff.android.domain.service

interface ImageHandler {
    suspend fun saveImages(uris: String, id: Long): String?
    fun shareImage(imagePath: String)
}
