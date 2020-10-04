package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

interface GetMessage {
    suspend operator fun invoke(messageId: Long): SimpleResult<Message>
}

class GetMessageImpl(
    private val messageRepository: MessageRepository
) : GetMessage {
    override suspend fun invoke(messageId: Long): SimpleResult<Message> {
        return messageRepository.getMessage(messageId)
    }
}