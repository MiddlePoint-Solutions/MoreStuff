package co.softov.morestuff.androidApp.data.repository


import co.softov.morestuff.androidApp.data.mapper.MessageDbMapper
import co.softov.morestuff.androidApp.data.utils.TimeUtils
import co.softov.morestuff.androidApp.domain.model.Message
import co.softov.morestuff.androidApp.domain.model.Result.Failure
import co.softov.morestuff.androidApp.domain.model.Result.Success
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.MessageDoesNotExist
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import co.softov.morestuff.db.StuffDb

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val lastInsertId: Long get() = messageQueries.lastInsertRowId().executeAsOne()

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
        messageQueries.insertMessage(
            task_id = taskId,
            schedule_id = scheduleId,
            create_time = TimeUtils.currentLocalDateTimeString,
            content_type = contentType,
            content = content
        )
        return getLastCreatedMessageForTask(taskId)
    }

    private fun getLastCreatedMessageForTask(taskId: Long): SimpleResult<Message> {
        return when (val message =
            messageQueries.selectCurrentTaskMessage(taskId).executeAsOneOrNull()) {
            null -> Failure(MessageDoesNotExist)
            else -> Success(mapMessageDb(message))
        }
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
            is Failure -> TODO("Return error!")
        }
    }

    private fun getCurrentTaskMessageId(taskId: Long): SimpleResult<Long> {
        val id = messageQueries.selectCurrentTaskMessageId(task_id = taskId).executeAsOneOrNull()
        return id?.let { Success(it) } ?: Failure(MessageDoesNotExist)
    }


}