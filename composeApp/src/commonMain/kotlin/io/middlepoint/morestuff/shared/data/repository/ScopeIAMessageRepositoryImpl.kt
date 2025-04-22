package io.middlepoint.morestuff.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import io.middlepoint.morestuff.shared.domain.repository.ScopeIAMessageRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.service.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ScopeIAMessageRepositoryImpl(
    database: StuffDb,
    private val mapper: DataMappers,
    private val timeManager: TimeManager,
) : ScopeIAMessageRepository {

    private val iaMessageQueries = database.iaMessageQueries
    private val messageDataQueries = database.messageDataQueries
    private val lastInsertId: Long get() = iaMessageQueries.lastInsertRowId().executeAsOne()


    override fun getIAMessagesByScopeId(scopeId: Long): Flow<List<IAMessage>> =
        iaMessageQueries.selectIAMessagesByScopeId(scopeId, mapper = mapper.iaMessageDbMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)

    override suspend fun getIAMessageById(messageId: Long): Either<Failure, IAMessage> {
        val message = iaMessageQueries.selectIAMessageById(
            id = messageId,
            mapper = mapper.iaMessageDbMapper
        ).executeAsOneOrNull()
        return when (message) {
            null -> ScopeIAMessageRepository.MessageDoesNotExist.left()
            else -> message.right()
        }
    }

    override suspend fun createScopeIAMessage(
        scopeId: Long,
        contentType: Int,
        messageData: MessageData?,
        content: String
    ): Either<Failure, IAMessage> = iaMessageQueries.transactionWithResult {
        logger.d { "IAMessageRepository: Creating IA message with contentType: $contentType, content: $content" }
        iaMessageQueries.insertIAMessage(
            scope_id = scopeId,
            create_time = timeManager.getCreateTime(),
            content_type = contentType,
            content = content
        )
        val messageId = lastInsertId
        logger.d { "IAMessageRepository: IA message created with ID: $messageId" }
        messageData?.let {
            messageDataQueries.insertMessageData(
                message_id = messageId,
                file_path = messageData.filePath,
                creation_time = timeManager.getCreateTime(),
                data_type = messageData.messageType.name,
            )
        }
        iaMessageQueries.selectIAMessageById(
            id = messageId,
            mapper = mapper.iaMessageDbMapper
        ).executeAsOne().right()

    }

    override suspend fun updateIAMessageReply(
        messageId: Long,
        replyType: Int?,
        replyContent: String?,
        replyTime: String?
    ) {
        iaMessageQueries.updateIAMessageReply(
            reply_type = replyType,
            reply_content = replyContent,
            reply_time = replyTime,
            id = messageId
        )
    }

    override suspend fun updateScopeIAMessageContent(
        messageId: Long,
        content: String
    ): Either<Failure, Boolean> {
        iaMessageQueries.updateIAMessageContent(
            content = content,
            id = messageId
        )
        return Either.Right(true)
    }

    override suspend fun deleteScopeIAMessage(messageId: Long) {
        iaMessageQueries.deleteIAMessage(messageId)
    }

    override fun getScopeIAChatMessages(scopeId: Long): List<IAMessage> =
        iaMessageQueries.selectIAMessagesByScopeIdAndContentTypes(
            scopeId,
            listOf(
                ContentType.TASK_MESSAGE.value,
                ContentType.APP_TASK_MESSAGE.value,
                ContentType.AI_TASK_MESSAGE.value
            ),
            mapper = mapper.iaMessageDbMapper
        ).executeAsList()

    override fun getScopeIAChatMessagesFlow(scopeId: Long): Flow<List<IAMessage>> =
        iaMessageQueries.selectIAMessagesByScopeIdAndContentTypes(
            scopeId,
            listOf(
                ContentType.TASK_MESSAGE.value,
                ContentType.APP_TASK_MESSAGE.value,
                ContentType.AI_TASK_MESSAGE.value
            ),
            mapper = mapper.iaMessageDbMapper
        ).asFlow().mapToList(Dispatchers.IO)
}