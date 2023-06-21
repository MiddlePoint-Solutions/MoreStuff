package co.softov.morestuff.android.data.repository


import arrow.core.Either
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.data.mapper.SelectMasterMessagesMapper
import co.softov.morestuff.android.data.mapper.SelectMessageByIdMapper
import co.softov.morestuff.android.data.mapper.SelectMessageByTaskIdMapper
import co.softov.morestuff.android.data.mapper.SelectTaskMessagesByContentTypeMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.repository.MessageDoesNotExist
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult
import co.softov.morestuff.db.StuffDb
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper,
    private val mapMessageTaskChatDb: SelectTaskMessagesByContentTypeMapper,
    private val selectMasterMessagesMapper: SelectMasterMessagesMapper,
    private val selectMessageByTaskIdMapper: SelectMessageByTaskIdMapper,
    private val selectMessageByIdMapper: SelectMessageByIdMapper,
    private val timeManager: TimeManager,
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val urlMetadataQueries = database.urlMetadataQueries
    private val lastInsertId: Long get() = messageQueries.lastInsertRowId().executeAsOne()

    override fun getAllMessages(): Flow<List<Message>> {
//        return messageQueries.selectAll().asFlow().mapToList()
        return messageQueries.selectMasterMessages().asFlow().mapToList()
            .map { mapList(it, selectMasterMessagesMapper) }
    }

    override fun getTaskChatMessagesFlow(taskId: Long): Flow<List<Message>> {
        return messageQueries.selectTaskMessagesByContentType(
            taskId,
            ContentType.TASK_MESSAGE.value
        ).asFlow().mapToList().map { mapList(it, mapMessageTaskChatDb) }
    }

    override fun getTaskMessagesFlow(taskId: Long): Flow<List<Message>> {
        return messageQueries.selectMessageByTaskId(taskId)
            .asFlow().mapToList().map { mapList(it, selectMessageByTaskIdMapper) }
    }

    override suspend fun getActiveReminderMessages(): List<Message> {
        return messageQueries.selectActiveReminderMessages().executeAsList()
            .map { mapMessageDb(it) }
    }

    override suspend fun getMessage(messageId: Long): Either<Failure, Message> {
        return when (val message =
            messageQueries.selectMessageById(messageId).executeAsOneOrNull()) {
            null -> Either.Left(MessageDoesNotExist)
            else -> Either.Right(selectMessageByIdMapper(message))
        }
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


    override suspend fun fetchOpenGraphMetadata(inputUrl: String): OpenGraphResult? =
        withContext(Dispatchers.IO) {
            try {
                val userAgent = "Mozilla"
                val referrer = "http://www.google.com"
                val timeout = 10000
                val docSelectQuery = "meta[property^=og:]"
                val openGraphKey = "content"
                val property = "property"
                val ogImage = "og:image"
                val ogDescription = "og:description"
                val ogUrl = "og:url"
                val ogTitle = "og:title"
                val ogSiteName = "og:site_name"
                val ogType = "og:type"
                var url = inputUrl

                if (!url.contains("http")) {
                    url = "http://$url"
                }

                val response = Jsoup.connect(url).ignoreContentType(true).userAgent(userAgent)
                    .referrer(referrer).timeout(timeout).followRedirects(true).execute()

                val doc = response.parse()

                val ogTags = doc.select(docSelectQuery)

                var openGraphResult = OpenGraphResult()

                ogTags.forEach { tag ->
                    openGraphResult = when (tag.attr(property)) {
                        ogImage -> openGraphResult.copy(image = tag.attr(openGraphKey))
                        ogDescription -> openGraphResult.copy(description = tag.attr(openGraphKey))
                        ogUrl -> openGraphResult.copy(url = tag.attr(openGraphKey))
                        ogTitle -> openGraphResult.copy(title = tag.attr(openGraphKey))
                        ogSiteName -> openGraphResult.copy(siteName = tag.attr(openGraphKey))
                        ogType -> openGraphResult.copy(type = tag.attr(openGraphKey))
                        else -> openGraphResult
                    }
                }

                return@withContext openGraphResult
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }

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

    override suspend fun getMetadata(messageId: Long): Pair<String, OpenGraphResult>? {
        val metadata =
            urlMetadataQueries.getUrlMetadata(messageId).executeAsOneOrNull() ?: return null
        val url = metadata.url
        val jsonData = metadata.json_data
        val openGraphResult = Json.decodeFromString<OpenGraphResult>(jsonData)
        return url to openGraphResult
    }

}