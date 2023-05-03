package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskChatDebugMessagesUseCase {
    operator fun invoke(taskId: Long): Flow<List<Message>>
}

class GetTaskChatDebugMessagesUseCaseImpl(
    private val messageRepository: MessageRepository,
) : GetTaskChatDebugMessagesUseCase {
    override fun invoke(taskId: Long): Flow<List<Message>> {
        return messageRepository.getTaskChatDebugMessagesFlow(taskId)
    }
}