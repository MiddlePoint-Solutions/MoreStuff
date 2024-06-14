package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface ClearActiveReminderMessagesUseCase {
    suspend operator fun invoke()
}

class ClearActivePendingMessagesUseCaseImpl(
    private val messageRepository: MessageRepository
) : ClearActiveReminderMessagesUseCase {

    override suspend fun invoke() = messageRepository.clearActiveReminderMessages()

}