package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
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