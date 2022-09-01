package co.softov.morestuff.android.domain.usecase.message

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.repository.MessageRepository

interface SetScheduleResponseMessage {
    suspend operator fun invoke(
        taskId: Long,
        title: String,
        replyType: ReplyType
    ): Either<Failure, Boolean>
}

class AddReminderReplyMessageImpl(
    private val messageRepository: MessageRepository
) : SetScheduleResponseMessage {

    override suspend fun invoke(
        taskId: Long,
        title: String,
        replyType: ReplyType
    ): Either<Failure, Boolean> {
        messageRepository.addUserReplyMessage(taskId, replyType.value, title)
        return Either.Right(true)
    }
}