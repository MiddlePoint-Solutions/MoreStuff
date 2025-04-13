package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
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



