package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.OpenGraphResult

interface OpenGraphInterface {
    suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult?
}
