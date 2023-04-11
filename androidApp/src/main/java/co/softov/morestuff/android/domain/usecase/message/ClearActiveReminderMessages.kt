package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.repository.MessageRepository

interface ClearActiveReminderMessages {
    suspend operator fun invoke()
}

class ClearActivePendingMessagesImpl(
    private val messageRepository: MessageRepository
) : ClearActiveReminderMessages {

    override suspend fun invoke() = messageRepository.clearActiveReminderMessages()

}