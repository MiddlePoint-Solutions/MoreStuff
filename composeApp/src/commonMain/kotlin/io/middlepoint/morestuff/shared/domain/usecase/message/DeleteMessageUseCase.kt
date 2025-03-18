package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.repository.MessageRepository


interface DeleteMessageUseCase {
    suspend operator fun invoke(messageId: Long)
}

class DeleteMessageUseCaseImpl(
    private val messageRepository: MessageRepository,
) : DeleteMessageUseCase {
    override suspend fun invoke(messageId: Long) {
        messageRepository.deleteMessage(messageId)
    }
}
