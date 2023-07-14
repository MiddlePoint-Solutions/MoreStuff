package co.softov.morestuff.android.domain.repository


import android.net.Uri
import arrow.core.Either
import co.softov.morestuff.android.domain.model.MessageWithData
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.FeatureFailure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.OpenGraphResult
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
        messageWithData: MessageWithData?,
        content: String,
    ): Either<Failure, Message>

    suspend fun addUserReplyMessage(taskId: Long, replyType: Int, replyContent: String)

    suspend fun clearActiveReminderMessages()

    suspend fun countActiveReminderMessages(): Int

    suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult?

    suspend fun insertUrlMetadata(url: String, openGraphResult: OpenGraphResult, messageId: Long)

    suspend fun insertMessageData(messageWithData: MessageWithData)

    suspend fun handleImages(
        uris: Uri,
        timeManager: TimeManager,
        id: Long,
    ): MessageWithData

    suspend fun deleteMessage(messageId: Long)

}

object MessageDoesNotExist : FeatureFailure