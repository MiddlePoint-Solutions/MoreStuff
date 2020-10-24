package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

interface SetScheduleResponseMessage {
    suspend operator fun invoke(taskId: Long, title: String, replyType: ReplyType): SimpleResult<Boolean>
}

class AddReminderReplyMessageImpl(
    private val messageRepository: MessageRepository
) : SetScheduleResponseMessage {

    override suspend fun invoke(
        taskId: Long,
        title: String,
        replyType: ReplyType
    ): SimpleResult<Boolean> {
        messageRepository.addUserReplyMessage(taskId, replyType.value, title)
        return Result.Success(true)
    }
}