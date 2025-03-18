package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.service.OpenGraphFetcher

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

