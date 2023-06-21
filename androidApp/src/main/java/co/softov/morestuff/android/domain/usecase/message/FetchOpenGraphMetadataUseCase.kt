package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult

interface FetchOpenGraphMetadataUseCase {
    suspend operator fun invoke(inputUrl: String, messageId: Long): OpenGraphResult?
}

class FetchOpenGraphMetadataUseCaseImpl(
    private val messageRepository: MessageRepository,
    private val getMetadataUseCase: GetMetadataUseCase
) : FetchOpenGraphMetadataUseCase {
    override suspend fun invoke(inputUrl: String, messageId: Long): OpenGraphResult? {
        val existingMetadata = getMetadataUseCase(messageId)

        if (existingMetadata?.first == inputUrl) {
            return existingMetadata.second
        }

        return messageRepository.fetchOpenGraphMetadata(inputUrl)
    }
}

