package co.softov.morestuff.androidApp.data.repository




import co.softov.morestuff.androidApp.data.mapper.MessageDbMapper
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.enums.Message
import co.softov.morestuff.androidApp.domain.enums.ReplyType
import co.softov.morestuff.androidApp.domain.model.Result
import co.softov.morestuff.androidApp.domain.repository.MessageDoesNotExist
import co.softov.morestuff.androidApp.domain.repository.MessageRepository
import co.softov.morestuff.db.StuffDb
import java.util.Calendar

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper
) : MessageRepository {

    private val messageQueries = database.messageQueries

    override suspend fun getMessage(messageId: Long): SimpleResult<co.softov.morestuff.androidApp.domain.model.Message> {
        return when (val message =
            messageQueries.selectMessageById(messageId).executeAsOneOrNull()) {
            null -> Result.Failure(MessageDoesNotExist)
            else -> Result.Success(mapMessageDb(message))
        }
    }

    override suspend fun getMessagesForTask(taskId: Long): SimpleResult<List<co.softov.morestuff.androidApp.domain.model.Message>> {
        return Result.Success(
            messageQueries.selectMessageByTaskId(taskId).executeAsList().map { mapMessageDb(it) })
    }

    override suspend fun createTaskMessage(
        taskId: Long,
        content: String
    ): SimpleResult<Boolean> {
        val currentTime = Calendar.getInstance().timeInMillis
        messageQueries.insertMessage(
            taskId,
            Message.USER_NEW_TASK,
            currentTime,
            content,
            ReplyType.NONE
        )
        return Result.Success(true)
    }

    override suspend fun createScheduledMessageForTask(
        taskId: Long,
        content: String
    ): SimpleResult<co.softov.morestuff.androidApp.domain.model.Message> {
        val currentTime = Calendar.getInstance().timeInMillis
        messageQueries.insertMessage(
            taskId,
            Message.TASK_REMINDER,
            currentTime,
            content,
            ReplyType.NONE
        )
        val messageId = messageQueries.lastInsertRowId().executeAsOne()
        return getMessage(messageId)
    }

    override suspend fun createConfirmationMessageForTask(taskId: Long, content: String) {
        val currentTime = Calendar.getInstance().timeInMillis
        messageQueries.insertMessage(
            taskId,
            Message.CONFIRM_NEW_TASK,
            currentTime,
            content,
            ReplyType.NONE
        )
    }

    override suspend fun addReminderReplyMessage(
        taskId: Long,
        content: String,
        replyType: ReplyType
    ) {
        val currentTime = Calendar.getInstance().timeInMillis
        val messageId =
            messageQueries.selectCurrentTaskMessageId(task_id = taskId).executeAsOneOrNull()
        messageId?.let {
            messageQueries.updateTaskMessageReply(replyType, content, currentTime, messageId)
        }
    }
}