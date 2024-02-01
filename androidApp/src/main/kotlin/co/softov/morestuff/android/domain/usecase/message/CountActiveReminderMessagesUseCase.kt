package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository

interface CountActiveReminderMessagesUseCase {
    suspend operator fun invoke(): Int
}

class CountActiveReminderMessagesUseCaseImpl(
    private val messageRepository: MessageRepository
) : CountActiveReminderMessagesUseCase {

    override suspend fun invoke(): Int = messageRepository.countActiveReminderMessages()
}