package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetMessages {
    suspend operator fun invoke(): Flow<List<Message>>
}

class GetMessagesImpl(
    private val messageRepository: MessageRepository
) : GetMessages {
    override suspend fun invoke(): Flow<List<Message>> {
        return messageRepository.getAllMessages()
    }
} 



