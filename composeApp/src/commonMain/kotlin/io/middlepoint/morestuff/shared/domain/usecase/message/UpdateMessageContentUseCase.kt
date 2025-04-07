package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface UpdateMessageContentUseCase {
    suspend operator fun invoke(messageId: Long, content: String): Either<Failure, Boolean>
}

class UpdateMessageContentUseCaseImpl(
    private val messageRepository: MessageRepository
) : UpdateMessageContentUseCase {
    override suspend fun invoke(messageId: Long, content: String): Either<Failure, Boolean> {
        return messageRepository.updateMessageContent(messageId, content)
    }
}