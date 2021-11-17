package co.softov.morestuff.android.domain.repository


import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.SimpleResult
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun getAllMessages(): Flow<List<Message>>

    suspend fun getActiveScheduleMessages(startTime: String): Flow<List<Message>>

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