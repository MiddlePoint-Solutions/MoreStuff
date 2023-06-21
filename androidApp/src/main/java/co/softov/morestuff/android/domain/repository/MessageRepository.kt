package co.softov.morestuff.android.domain.repository


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun getAllMessages(): Flow<List<Message>>

    suspend fun getActiveReminderMessages(): List<Message>

    suspend fun getMessage(messageId: Long): Either<Failure, Message>

    fun getTaskMessagesFlow(taskId: Long): Flow<List<Message>>

    fun getTaskChatMessagesFlow(taskId: Long): Flow<List<Message>>

    suspend fun createMessage(
        taskId: Long,
        scheduleId: Long,
        contentType: Int,
        content: String,
    ): Either<Failure, Message>

    suspend fun addUserReplyMessage(taskId: Long, replyType: Int, replyContent: String)

    suspend fun clearActiveReminderMessages()

    suspend fun countActiveReminderMessages(): Int
    suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult?
    suspend fun insertUrlMetadata(url: String, openGraphResult: OpenGraphResult, messageId: Long)


}

object MessageDoesNotExist : FeatureFailure