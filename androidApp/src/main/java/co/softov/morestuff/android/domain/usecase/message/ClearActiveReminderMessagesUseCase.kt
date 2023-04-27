package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository

interface ClearActiveReminderMessagesUseCase {
    suspend operator fun invoke()
}

class ClearActivePendingMessagesUseCaseImpl(
    private val messageRepository: MessageRepository
) : ClearActiveReminderMessagesUseCase {

    override suspend fun invoke() = messageRepository.clearActiveReminderMessages()

}