package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.android.domain.model.OpenGraphResult
import io.middlepoint.morestuff.android.domain.service.OpenGraphFetcher

interface FetchOpenGraphMetadataUseCase {
    suspend operator fun invoke(inputUrl: String): OpenGraphResult?
}

class FetchOpenGraphMetadataUseCaseImpl(
    private val openGraphFetcher: OpenGraphFetcher
) : FetchOpenGraphMetadataUseCase {
    override suspend fun invoke(inputUrl: String): OpenGraphResult? {
        return openGraphFetcher.fetchOpenGraphMetadata(inputUrl)
    }
}

