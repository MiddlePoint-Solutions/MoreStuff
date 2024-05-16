package io.middlepoint.morestuff.android.domain.usecase.message

import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.enums.ReplyType
import io.middlepoint.morestuff.android.domain.repository.MessageRepository

interface SetScheduleMessageResponseUseCase {
    suspend operator fun invoke(
        taskIds: List<Long>,
        title: String,
        replyType: ReplyType
    ): Either<Failure, Boolean>
}

class SetScheduleMessageResponseUseCaseImpl(
    private val messageRepository: MessageRepository
) : SetScheduleMessageResponseUseCase {

    override suspend fun invoke(
        taskIds: List<Long>,
        title: String,
        replyType: ReplyType
    ): Either<Failure, Boolean> {
        taskIds.forEach {
            messageRepository.addUserReplyMessage(it, replyType.value, title)
        }
        return Either.Right(true)
    }
}