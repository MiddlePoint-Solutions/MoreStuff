package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult

interface OpenGraphFetcher {
    suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult?
}
