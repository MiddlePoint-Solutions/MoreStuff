package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface CountActiveReminderMessagesUseCase {
    suspend operator fun invoke(): Int
}

class CountActiveReminderMessagesUseCaseImpl(
    private val messageRepository: MessageRepository
) : CountActiveReminderMessagesUseCase {

    override suspend fun invoke(): Int = messageRepository.countActiveReminderMessages()
}