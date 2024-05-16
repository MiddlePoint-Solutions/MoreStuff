package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
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
