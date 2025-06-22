package io.middlepoint.morestuff.shared.domain.repository


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.core.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

  suspend fun getMessage(messageId: Uuid): Either<Failure, Message>
  suspend fun getTaskChatMessages(taskId: Uuid): List<Message>
  fun getTaskMessagesFlow(taskId: Uuid): Flow<List<Message>>
  fun getTaskChatMessagesFlow(taskId: Uuid): Flow<List<Message>>

  suspend fun createMessage(
    taskId: Uuid,
    scheduleId: Uuid?,
    contentType: Int,
    legacyCreatedAt: String?,
    messageExtra: MessageExtra?,
    content: String,
  ): Either<Failure, Message>

  suspend fun insertUrlMetadata(url: String, openGraphResult: OpenGraphResult, messageId: Uuid)
  suspend fun deleteMessage(messageId: Uuid)
  suspend fun updateMessageContent(
    messageId: Uuid,
    content: String,
  ): Either<Failure, Boolean>
}

object MessageDoesNotExist : FeatureFailure