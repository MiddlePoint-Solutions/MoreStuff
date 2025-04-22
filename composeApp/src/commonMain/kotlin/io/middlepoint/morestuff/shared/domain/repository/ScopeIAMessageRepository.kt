package io.middlepoint.morestuff.shared.domain.repository

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.FeatureFailure
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import kotlinx.coroutines.flow.Flow

interface ScopeIAMessageRepository {

  fun getIAMessagesByScopeId(scopeId: Long): Flow<List<IAMessage>>

  suspend fun getIAMessageById(messageId: Long): Either<Failure, IAMessage>

  suspend fun createScopeIAMessage(
    scopeId: Long,
    contentType: Int,
    messageData: MessageData?,
    content: String
  ): Either<Failure, IAMessage>

  suspend fun updateIAMessageReply(
    messageId: Long,
    replyType: Int?,
    replyContent: String?,
    replyTime: String?
  )

  suspend fun updateScopeIAMessageContent(
    messageId: Long,
    content: String
  ): Either<Failure, Boolean>

  suspend fun deleteScopeIAMessage(messageId: Long)
  fun getScopeIAChatMessages(scopeId: Long): List<IAMessage>
  fun getScopeIAChatMessagesFlow(scopeId: Long): Flow<List<IAMessage>>

  object MessageDoesNotExist : FeatureFailure
}