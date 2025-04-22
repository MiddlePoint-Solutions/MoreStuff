package io.middlepoint.morestuff.shared.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository

interface GetMessageUseCase {
    suspend operator fun invoke(messageId: Uuid): Either<Failure, Message>
}

class GetMessageImpl(
    private val messageRepository: MessageRepository
) : GetMessageUseCase {
    override suspend fun invoke(messageId: Uuid): Either<Failure, Message> {
        return messageRepository.getMessage(messageId)
    }
}