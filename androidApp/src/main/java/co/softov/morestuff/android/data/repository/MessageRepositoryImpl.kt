package co.softov.morestuff.android.data.repository


import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Result.Failure
import co.softov.morestuff.android.domain.model.Result.Success
import co.softov.morestuff.android.domain.model.SimpleResult
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
        return messageQueries.selectAll().asFlow().mapToList().map { mapList(it, mapMessageDb).reversed() }
    }

    override suspend fun getActiveScheduleMessages(startTime: String): Flow<List<Message>> {
        return messageQueries.selectActiveScheduleMessages(startTime).asFlow().mapToList().map { mapList(it, mapMessageDb).reversed() }
    }

    override suspend fun getMessage(messageId: Long): SimpleResult<Message> {
        return when (val message =
            messageQueries.selectMessageById(messageId).executeAsOneOrNull()) {
            null -> Failure(MessageDoesNotExist)
            else -> Success(mapMessageDb(message))
        }
    }

    override suspend fun getMessagesForTask(taskId: Long): SimpleResult<List<Message>> {
        return Success(
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
    ): SimpleResult<Message> {
        val messageId: Long = messageQueries.transactionWithResult {
            messageQueries.insertMessage(
                task_id = taskId,
                schedule_id = scheduleId,
                create_time = TimeUtils.currentLocalDateTimeString,
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
        when (val messageId = getCurrentTaskMessageId(taskId)) {
            is Success -> {
                messageQueries.updateTaskMessageReply(
                    reply_type = replyType,
                    reply_content = replyContent,
                    reply_time = TimeUtils.currentLocalDateTimeString,
                    id = messageId.value
                )
            }
            is Failure -> Failure(MessageDoesNotExist)
        }
    }

    private fun getLastCreatedMessageForTask(taskId: Long): SimpleResult<Message> {
        return when (val message =
            messageQueries.selectCurrentTaskMessage(taskId).executeAsOneOrNull()) {
            null -> Failure(MessageDoesNotExist)
            else -> Success(mapMessageDb(message))
        }
    }

    private fun getCurrentTaskMessageId(taskId: Long): SimpleResult<Long> {
        val id = messageQueries.selectCurrentTaskMessageId(task_id = taskId).executeAsOneOrNull()
        return id?.let { Success(it) } ?: Failure(MessageDoesNotExist)
    }


}