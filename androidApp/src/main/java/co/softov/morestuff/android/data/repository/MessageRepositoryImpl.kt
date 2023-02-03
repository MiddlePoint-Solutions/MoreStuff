package co.softov.morestuff.android.data.repository


import arrow.core.Either
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageDoesNotExist
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val lastInsertId: Long get() = messageQueries.lastInsertRowId().executeAsOne()

    override suspend fun getAllMessages(): Flow<List<Message>> {
        return messageQueries.selectAll().asFlow().mapToList()
            .map { mapList(it, mapMessageDb).reversed() }
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

    override suspend fun getMessagesForTask(taskId: Long): Either<Failure, List<Message>> {
        return Either.Right(
            messageQueries.selectMessageByTaskId(taskId)
                .executeAsList()
                .map { mapMessageDb(it) }
        )
    }

    override suspend fun createMessage(
        taskId: Long,
        scheduleId: Long,
        contentType: Int,
        content: String
    ): Either<Failure, Message> {
        val messageId: Long = messageQueries.transactionWithResult {
            messageQueries.insertMessage(
                task_id = taskId,
                schedule_id = scheduleId,
                create_time = TimeUtils.getCreateTime(),
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
        replyContent: String
    ) {
        // TODO: this logic should be moved into 2 use cases
        when (val messageId = getCurrentTaskMessageId(taskId)) {
            is Either.Right -> {
                messageQueries.updateTaskMessageReply(
                    reply_type = replyType,
                    reply_content = replyContent,
                    reply_time = TimeUtils.nowUtcInstantString,
                    id = messageId.value
                )
            }
            is Either.Left -> MessageDoesNotExist
        }
    }

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