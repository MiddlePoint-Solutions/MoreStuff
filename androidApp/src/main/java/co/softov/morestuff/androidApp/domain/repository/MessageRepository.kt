package co.softov.morestuff.androidApp.domain.repository


import co.softov.morestuff.androidApp.domain.Failure
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Message

interface MessageRepository {

    suspend fun getMessage(messageId: Long): SimpleResult<Message>

    suspend fun getMessagesForTask(taskId: Long): SimpleResult<List<Message>>

    suspend fun createTaskMessage(taskId: Long, content: String): SimpleResult<Boolean>

    suspend fun createScheduledMessageForTask(
        taskId: Long,
        content: String
    ): SimpleResult<Message>

    suspend fun createConfirmationMessageForTask(taskId: Long, content: String)

    suspend fun addReminderReplyMessage(taskId: Long, content: String, replyType: ReplyType)
}

object MessageDoesNotExist: Failure.FeatureFailure()