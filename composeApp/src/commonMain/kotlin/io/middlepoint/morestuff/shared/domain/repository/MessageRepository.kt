package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.Message
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

  fun getAllMessages(): Flow<List<Message>>
  suspend fun getMessage(messageId: Long): Either<Failure, Message>
  fun getTaskChatMessages(taskId: Long): List<Message>
  fun getTaskMessagesFlow(taskId: Long): Flow<List<Message>>
  fun getTaskChatMessagesFlow(taskId: Long): Flow<List<Message>>
  fun getLastMessageFlow(contentType: ContentType): Flow<Message?>
  suspend fun createMessage(
    taskId: Long,
    scheduleId: Long,
    contentType: Int,
    messageData: MessageData?,
    content: String,
  ): Either<Failure, Message>

  suspend fun addUserReplyMessage(taskId: Long, replyType: Int, replyContent: String)
  suspend fun clearActiveReminderMessages()
  suspend fun countActiveReminderMessages(): Int
  suspend fun insertUrlMetadata(url: String, openGraphResult: OpenGraphResult, messageId: Long)
  suspend fun insertMessageData(messageData: MessageData)
  suspend fun deleteMessage(messageId: Long)
  suspend fun updateMessageContent(
    messageId: Long,
    content: String,
  ): Either<Failure, Boolean>

}

object MessageDoesNotExist : FeatureFailure