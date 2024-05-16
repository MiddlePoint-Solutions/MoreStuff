package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
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



