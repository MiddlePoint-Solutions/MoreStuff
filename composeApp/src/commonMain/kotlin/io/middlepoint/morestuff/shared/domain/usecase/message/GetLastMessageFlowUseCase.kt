package io.middlepoint.morestuff.shared.domain.usecase.message

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

interface GetLastMessageFlowUseCase {
    operator fun invoke(contentType: ContentType): Flow<Message?>
}

class GetLastMessageFlowUseCaseImpl(
    private val messageRepository: MessageRepository
) : GetLastMessageFlowUseCase {
    override fun invoke(contentType: ContentType): Flow<Message?> {
        return messageRepository.getLastMessageFlow(contentType)
    }
}