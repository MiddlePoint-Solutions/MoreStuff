package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.repository.MessageRepository

interface GetMessageUseCase {
    suspend operator fun invoke(messageId: Long): Either<Failure, Message>
}

class GetMessageImpl(
    private val messageRepository: MessageRepository
) : GetMessageUseCase {
    override suspend fun invoke(messageId: Long): Either<Failure,Message> {
        return messageRepository.getMessage(messageId)
    }
}