package co.softov.morestuff.androidApp.domain.repository


import co.softov.morestuff.androidApp.domain.Failure
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.model.SimpleResult

interface MessageRepository {

    suspend fun getMessage(messageId: Long): SimpleResult<Message>

    suspend fun getMessagesForTask(taskId: Long): SimpleResult<List<Message>>

    suspend fun createMessage(
        taskId: Long,
        scheduleId: Long,
        contentType: Int,
        content: String
    ): SimpleResult<Message>

    suspend fun addUserReplyMessage(taskId: Long, replyType: Int, replyContent: String)
}

object MessageDoesNotExist : Failure.FeatureFailure()