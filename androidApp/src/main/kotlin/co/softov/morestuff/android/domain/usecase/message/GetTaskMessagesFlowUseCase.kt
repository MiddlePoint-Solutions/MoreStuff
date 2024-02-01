package co.softov.morestuff.android.domain.usecase.message

import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetTaskMessagesFlowUseCase {
    operator fun invoke(taskId: Long): Flow<List<Message>>
}

class GetTaskMessagesFlowUseCaseImpl(
    private val messageRepository: MessageRepository
) : GetTaskMessagesFlowUseCase {
    override fun invoke(taskId: Long): Flow<List<Message>> {
        return messageRepository.getTaskMessagesFlow(taskId)
    }
}