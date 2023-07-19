package co.softov.morestuff.android.data.repository


import arrow.core.Either
import arrow.core.left
import arrow.core.right
import co.softov.morestuff.android.data.mapper.MessageDataMapper
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.repository.MessageDoesNotExist
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper,
    private val messageDataMapper: MessageDataMapper,
    private val timeManager: TimeManager,
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val urlMetadataQueries = database.urlMetadataQueries
    private val messageDataQueries = database.messageDataQueries
    private val lastInsertId: Long get() = messageQueries.lastInsertRowId().executeAsOne()

    override fun getAllMessages(): Flow<List<Message>> {
        return messageQueries.selectMasterMessages(mapper = messageDataMapper)
            .asFlow()
            .mapToList()
    }

    override fun getTaskChatMessagesFlow(taskId: Long): Flow<List<Message>> {
        return messageQueries.selectTaskMessagesByContentType(
            taskId,
            ContentType.TASK_MESSAGE.value,
            mapper = messageDataMapper
        ).asFlow().mapToList()
    }

    override fun getTaskMessagesFlow(taskId: Long): Flow<List<Message>> {
        return messageQueries.selectMessageByTaskId(taskId, mapper = messageDataMapper)
            .asFlow().mapToList()
    }

    override suspend fun getActiveReminderMessages(): List<Message> {
        return messageQueries.selectActiveReminderMessages().executeAsList()
            .map { mapMessageDb(it) }
    }

    override suspend fun getMessage(messageId: Long): Either<Failure, Message> {
        val message = messageQueries.selectMessageById(
            id = messageId,
            mapper = messageDataMapper
        ).executeAsOneOrNull()
        return when (message) {
            null -> MessageDoesNotExist.left()
            else -> message.right()
        }
    }

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
            mapper = messageDataMapper
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
        messageQueries.deleteMessage(messageId)
    }
}