package co.softov.morestuff.android.domain.service

import android.net.Uri

interface ImageHandler {
    suspend fun handleImages(uris: Uri, id: Long): String?
}
