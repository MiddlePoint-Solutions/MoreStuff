package io.middlepoint.morestuff.android.data.repository


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.android.data.mapper.DataMappers
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.model.MessageData
import io.middlepoint.morestuff.android.domain.model.OpenGraphResult
import io.middlepoint.morestuff.android.domain.repository.MessageDoesNotExist
import io.middlepoint.morestuff.android.domain.repository.MessageRepository
import io.middlepoint.morestuff.android.domain.service.TimeManager
import io.middlepoint.morestuff.db.StuffDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapper: DataMappers,
    private val timeManager: TimeManager,
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val urlMetadataQueries = database.urlMetadataQueries
    private val messageDataQueries = database.messageDataQueries
    private val lastInsertId: Long get() = messageQueries.lastInsertRowId().executeAsOne()

    override fun getAllMessages(): Flow<List<Message>> =
        messageQueries.selectMasterMessages(mapper = mapper.messageDataMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)

    override fun getTaskChatMessagesFlow(taskId: Long): Flow<List<Message>> =
        messageQueries.selectTaskMessagesByContentType(
            taskId,
            listOf(ContentType.TASK_MESSAGE.value, ContentType.APP_TASK_MESSAGE.value),
            mapper = mapper.messageDataMapper
        ).asFlow().mapToList(Dispatchers.IO)

    override fun getTaskMessagesFlow(taskId: Long): Flow<List<Message>> =
        messageQueries.selectMessageByTaskId(taskId, mapper = mapper.messageDataMapper)
            .asFlow()
            .mapToList(Dispatchers.IO)

    override suspend fun getMessage(messageId: Long): Either<Failure, Message> {
        val message = messageQueries.selectMessageById(
            id = messageId,
            mapper = mapper.messageDataMapper
        ).executeAsOneOrNull()
        return when (message) {
            null -> MessageDoesNotExist.left()
            else -> message.right()
        }
    }

    override fun getLastMessageFlow(contentType: ContentType): Flow<Message?> =
        messageQueries.selectLastTaskMessageByContentType(
            contentType.value,
            mapper = mapper.messageDbMapper
        ).asFlow().mapToOneOrNull(Dispatchers.IO)


    override suspend fun createMessage(
        taskId: Long,
        scheduleId: Long,
        contentType: Int,
        messageData: MessageData?,
        content: String,
    ): Either<Failure, Message> = messageQueries.transactionWithResult {
        messageQueries.insertMessage(
            task_id = taskId,
            schedule_id = scheduleId,
            create_time = timeManager.getCreateTime(),
            content_type = contentType,
            content = content
        )
        val messageId = lastInsertId
        messageData?.let {
            messageDataQueries.insertMessageData(
                message_id = messageId,
                file_path = messageData.filePath,
                creation_time = timeManager.getCreateTime(),
                data_type = messageData.messageType.name,
            )
        }
        messageQueries.selectMessageById(
            id = messageId,
            mapper = mapper.messageDataMapper
        ).executeAsOne().right()
    }

    override suspend fun addUserReplyMessage(
        taskId: Long,
        replyType: Int,
        replyContent: String,
    ) {
        // TODO: this logic should be moved into 2 use cases
        when (val messageId = getCurrentTaskMessageId(taskId, ContentType.TASK_REMINDER)) {
            is Either.Right -> {
                messageQueries.updateTaskMessageReply(
                    reply_type = replyType,
                    reply_content = replyContent,
                    reply_time = timeManager.nowUtcInstantString,
                    id = messageId.value
                )
            }

            is Either.Left -> MessageDoesNotExist
        }
    }

    override suspend fun clearActiveReminderMessages() {
        messageQueries.deleteActiveReminderMessages()
    }

    override suspend fun countActiveReminderMessages(): Int =
        messageQueries.countActiveReminderMessages().executeAsOne().toInt()

    private fun getCurrentTaskMessageId(
        taskId: Long,
        contentType: ContentType,
    ): Either<Failure, Long> =
        messageQueries.selectTaskMessage(
            task_id = taskId,
            content_type = contentType.value
        ).executeAsOneOrNull()?.let { Either.Right(it.id) } ?: Either.Left(MessageDoesNotExist)


    override suspend fun insertUrlMetadata(
        url: String,
        openGraphResult: OpenGraphResult,
        messageId: Long,
    ) {
        val openGraphResultJson = Json.encodeToString(openGraphResult)
        urlMetadataQueries.insertUrlMetadata(
            url = url,
            json_data = openGraphResultJson,
            message_id = messageId
        )
    }

    override suspend fun insertMessageData(messageData: MessageData) {
        messageDataQueries.insertMessageData(
            messageData.id,
            messageData.filePath,
            messageData.creationTime,
            messageData.messageType.name
        )
    }

    override suspend fun deleteMessage(messageId: Long) {
        val message = messageQueries.selectMessageById(
            id = messageId,
            mapper = mapper.messageDataMapper
        ).executeAsOneOrNull()

        val imagePath = message?.messageData?.filePath
        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                file.delete()
            }
        }
        messageQueries.deleteMessage(messageId)
    }

}