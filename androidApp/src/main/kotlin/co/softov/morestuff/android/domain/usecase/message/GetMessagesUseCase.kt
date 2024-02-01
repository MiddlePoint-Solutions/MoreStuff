package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetMessagesUseCase {
    operator fun invoke(): Flow<List<Message>>
}

class GetMessagesUseCaseImpl(
    private val messageRepository: MessageRepository,

) : GetMessagesUseCase {
    override fun invoke(): Flow<List<Message>> {
        return messageRepository.getAllMessages()
    }
} 



