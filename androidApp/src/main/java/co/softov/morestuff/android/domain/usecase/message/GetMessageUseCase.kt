package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.MessageRepository

interface GetMessageUseCase {
    suspend operator fun invoke(messageId: Long): SimpleResult<Message>
}

class GetMessageImpl(
    private val messageRepository: MessageRepository
) : GetMessageUseCase {
    override suspend fun invoke(messageId: Long): SimpleResult<Message> {
        return messageRepository.getMessage(messageId)
    }
}