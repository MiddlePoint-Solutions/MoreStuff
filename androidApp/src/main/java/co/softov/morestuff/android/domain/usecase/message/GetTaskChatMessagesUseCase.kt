package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
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
