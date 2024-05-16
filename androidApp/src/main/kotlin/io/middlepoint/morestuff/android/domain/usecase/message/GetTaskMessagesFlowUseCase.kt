package io.middlepoint.morestuff.android.domain.usecase.message

import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
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