package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository

interface CountActiveReminderMessages {
    suspend operator fun invoke(): Int
}

class CountActiveReminderMessagesImpl(
    private val messageRepository: MessageRepository
) : CountActiveReminderMessages {

    override suspend fun invoke(): Int = messageRepository.countActiveReminderMessages()
}