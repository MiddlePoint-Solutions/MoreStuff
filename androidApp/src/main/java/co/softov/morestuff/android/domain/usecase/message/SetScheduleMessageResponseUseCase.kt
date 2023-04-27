package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.repository.MessageRepository

interface SetScheduleMessageResponseUseCase {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        replyType: ReplyType
    ): Either<Failure, Boolean>
}

class SetScheduleMessageResponseUseCaseImpl(
    private val messageRepository: MessageRepository
) : SetScheduleMessageResponseUseCase {

    override suspend fun invoke(
        taskId: Long,
        title: String,
        replyType: ReplyType
    ): Either<Failure, Boolean> {
        messageRepository.addUserReplyMessage(taskId, replyType.value, title)
        return Either.Right(true)
    }
}