package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository

interface GetActiveScheduleMessages {
    suspend operator fun invoke(): List<Message>
}

class GetActiveMessagesImpl(
    private val messageRepository: MessageRepository
) : GetActiveScheduleMessages {
    override suspend fun invoke(): List<Message> {
        return messageRepository.getActiveReminderMessages()
    }
}