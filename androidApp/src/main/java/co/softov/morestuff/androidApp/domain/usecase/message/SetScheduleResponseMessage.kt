package co.softov.morestuff.androidApp.domain.usecase.message

import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageRepository

interface SetScheduleResponseMessage {
    suspend operator fun invoke(taskId: Long, title: String, type: ReplyType): SimpleResult<Boolean>
}

class AddReminderReplyMessageImpl(
    private val messageRepository: MessageRepository
) : SetScheduleResponseMessage {

    override suspend fun invoke(
        taskId: Long,
        title: String,
        type: ReplyType
    ): SimpleResult<Boolean> {
        messageRepository.addReminderReplyMessage(taskId, title, type)
        return Result.Success(true)
    }
}