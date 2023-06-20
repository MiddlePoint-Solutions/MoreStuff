package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult


interface GetMetadataUseCase {
    suspend operator fun invoke(messageId: Long): List<Pair<String, OpenGraphResult>>
}

class GetMetadataUseCaseImpl(
    private val messageRepository: MessageRepository
) : GetMetadataUseCase {
    override suspend fun invoke(messageId: Long): List<Pair<String, OpenGraphResult>> {
        return messageRepository.getMetadata(messageId)
    }
}



