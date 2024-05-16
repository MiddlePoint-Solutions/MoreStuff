package io.middlepoint.morestuff.android.domain.service

import io.middlepoint.morestuff.android.domain.model.OpenGraphResult

interface OpenGraphFetcher {
    suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult?
}
