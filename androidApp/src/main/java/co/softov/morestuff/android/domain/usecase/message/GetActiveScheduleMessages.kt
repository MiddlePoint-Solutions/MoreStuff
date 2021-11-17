package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetActiveScheduleMessages {
    suspend operator fun invoke(startTime: String): Flow<List<Message>>
}

class GetActiveScheduleMessagesImpl(
    private val messageRepository: MessageRepository
) : GetActiveScheduleMessages {
    override suspend fun invoke(startTime: String): Flow<List<Message>> {
        return messageRepository.getActiveScheduleMessages(startTime)
    }
}