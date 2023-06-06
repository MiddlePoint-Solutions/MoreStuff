package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult

interface FetchOpenGraphMetadataUseCase {
    suspend operator fun invoke(inputUrl: String, messageId: Long): OpenGraphResult?
}

class FetchOpenGraphMetadataUseCaseImpl(
    private val messageRepository: MessageRepository
) : FetchOpenGraphMetadataUseCase {
    override suspend fun invoke(inputUrl: String, messageId: Long): OpenGraphResult? {
        val existingMetadata = messageRepository.getMetadata(messageId)
            .find { it.first == inputUrl }?.second

        if (existingMetadata != null) {
            return existingMetadata
        }

        return messageRepository.fetchOpenGraphMetadata(inputUrl)
    }
}

