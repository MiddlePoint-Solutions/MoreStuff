package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.service.OpenGraphFetcher

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

