package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.service.OpenGraphInterface

interface FetchOpenGraphMetadataUseCase {
    suspend operator fun invoke(inputUrl: String): OpenGraphResult?
}

class FetchOpenGraphMetadataUseCaseImpl(
    private val openGraphInterface: OpenGraphInterface
) : FetchOpenGraphMetadataUseCase {
    override suspend fun invoke(inputUrl: String): OpenGraphResult? {

        return openGraphInterface.fetchOpenGraphMetadata(inputUrl)
    }
}

