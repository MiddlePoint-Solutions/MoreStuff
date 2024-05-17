package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskChatMessagesUseCase {
    operator fun invoke(taskId: Long): Flow<List<Message>>
}

class GetTaskChatMessagesUseCaseImpl(
    private val messageRepository: MessageRepository,
) : GetTaskChatMessagesUseCase {
    override fun invoke(taskId: Long): Flow<List<Message>> {
        return messageRepository.getTaskChatMessagesFlow(taskId)
    }
}
