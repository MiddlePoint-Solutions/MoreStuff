package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.db.MessageQueries

interface DeleteMessageUseCase {
    suspend operator fun invoke(messageId: Long)
}

class DeleteMessageUseCaseImpl(
    private val messageRepository: MessageRepository
) : DeleteMessageUseCase {
    override suspend fun invoke(messageId: Long) {
        messageRepository.deleteMessage(messageId)
    }
}
