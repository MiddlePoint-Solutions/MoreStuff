package co.softov.morestuff.android.domain.service

interface ImageHandler {
    suspend fun handleImages(uris: String, id: Long): String?
}
