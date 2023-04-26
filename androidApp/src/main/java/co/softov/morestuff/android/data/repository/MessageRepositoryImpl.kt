package co.softov.morestuff.android.data.repository


import arrow.core.Either
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageDoesNotExist
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper,
    private val timeManager: TimeManager,
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val lastInsertId: Long get() = messageQueries.lastInsertRowId().executeAsOne()

    override fun getAllMessages(): Flow<List<Message>> {
//        return messageQueries.selectAll().asFlow().mapToList()
        return messageQueries.selectMasterMessages().asFlow().mapToList()
            .map { mapList(it, mapMessageDb) }
    }
    override fun getTaskChatMessagesFlow(taskId: Long): Flow<List<Message>> {
        return messageQueries.selectMessageByContentType(taskId, ContentType.TASK_MESSAGE.value)
            .asFlow().mapToList().map { mapList(it, mapMessageDb) }
    }


    override suspend fun getActiveReminderMessages(): List<Message> {
        return messageQueries.selectActiveReminderMessages().executeAsList()
            .map { mapMessageDb(it) }
    }

    override suspend fun getMessage(messageId: Long): Either<Failure, Message> {
        return when (val message =
            messageQueries.selectMessageById(messageId).executeAsOneOrNull()) {
            null -> Either.Left(MessageDoesNotExist)
            else -> Either.Right(mapMessageDb(message))
        }
    }

    override fun getTaskMessagesFlow(taskId: Long): Flow<List<Message>> {
        return messageQueries.selectMessageByTaskId(taskId)
            .asFlow().mapToList().map { mapList(it, mapMessageDb) }
    }
    override suspend fun createMessage(
        taskId: Long,
        scheduleId: Long,
        contentType: Int,
        content: String,
    ): Either<Failure, Message> {
        val messageId: Long = messageQueries.transactionWithResult {
            messageQueries.insertMessage(
                task_id = taskId,
                schedule_id = scheduleId,
                create_time = timeManager.getCreateTime(),
                content_type = contentType,
                content = content
            )
            lastInsertId
        }
        return getMessage(messageId)
    }


    override suspend fun addUserReplyMessage(
        taskId: Long,
        replyType: Int,
        replyContent: String,
    ) {
        // TODO: this logic should be moved into 2 use cases
        when (val messageId = getCurrentTaskMessageId(taskId)) {
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

    private fun getLastCreatedMessageForTask(taskId: Long): Either<Failure, Message> {
        return when (val message =
            messageQueries.selectCurrentTaskMessage(taskId).executeAsOneOrNull()) {
            null -> Either.Left(MessageDoesNotExist)
            else -> Either.Right(mapMessageDb(message))
        }
    }

    private fun getCurrentTaskMessageId(taskId: Long): Either<Failure, Long> {
        val id = messageQueries.selectCurrentTaskMessageId(task_id = taskId).executeAsOneOrNull()
        return id?.let { Either.Right(it) } ?: Either.Left(MessageDoesNotExist)
    }


}