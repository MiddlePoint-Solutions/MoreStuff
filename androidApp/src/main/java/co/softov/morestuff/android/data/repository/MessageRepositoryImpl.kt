package co.softov.morestuff.android.data.repository


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import co.softov.morestuff.android.data.mapper.ImageMessageDataMapper
import co.softov.morestuff.android.data.mapper.MessageDbMapper
import co.softov.morestuff.android.data.mapper.SelectMasterMessagesMapper
import co.softov.morestuff.android.data.mapper.SelectMessageByIdMapper
import co.softov.morestuff.android.data.mapper.SelectMessageByTaskIdMapper
import co.softov.morestuff.android.data.mapper.SelectTaskMessagesByContentTypeMapper
import co.softov.morestuff.android.data.mapper.mapList
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageWithData
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.repository.MessageDoesNotExist
import co.softov.morestuff.android.domain.repository.MessageRepository
import co.softov.morestuff.android.domain.service.TimeManager
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
import java.io.FileOutputStream
import java.nio.file.Path

class MessageRepositoryImpl(
    database: StuffDb,
    private val mapMessageDb: MessageDbMapper,
    private val mapMessageTaskChatDb: SelectTaskMessagesByContentTypeMapper,
    private val selectMasterMessagesMapper: SelectMasterMessagesMapper,
    private val selectMessageByTaskIdMapper: SelectMessageByTaskIdMapper,
    private val selectMessageByIdMapper: SelectMessageByIdMapper,
    private val imageMessageDataMapper: ImageMessageDataMapper,
    private val timeManager: TimeManager,
    private val context: Context,
) : MessageRepository {

    private val messageQueries = database.messageQueries
    private val urlMetadataQueries = database.urlMetadataQueries
    private val messageDataQueries = database.messageDataQueries
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
            null -> MessageDoesNotExist.left()
            else -> imageMessageDataMapper(
                message.id,
                message.task_id,
                message.schedule_id,
                ContentType.valueOf(message.content_type.toString()),
                message.create_time,
                message.seen_time,
                message.content,
                ReplyType.valueOf(message.reply_type.toString()),
                message.reply_content,
                message.reply_time,
                message.json_data,
                message.id,
                message.data_type,
                message.creation_time,
                MessageDataType.valueOf(message.data_type.toString())

            ).right()
        }
    }


    override suspend fun createMessage(
        taskId: Long,
        scheduleId: Long,
        contentType: Int,
        messageWithData: MessageWithData?,
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
        messageWithData?.let {
            messageDataQueries.insertMessageData(
                message_id = messageId,
                file_path = messageWithData.filePath,
                creation_time = timeManager.getCreateTime(),
                data_type = messageWithData.messageType.name,
            )
        }
        messageQueries.selectMessageById(messageId).executeAsOneOrNull()
    }?.let { Either.Right(selectMessageByIdMapper(it)) }
        ?: Either.Left(MessageDoesNotExist)


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

    override suspend fun insertMessageData(messageWithData: MessageWithData) {
        messageDataQueries.insertMessageData(
            messageWithData.id,
            messageWithData.filePath,
            messageWithData.creationTime,
            messageWithData.messageType.name
        )
    }

    override suspend fun handleImages(
        uris: Uri,
        timeManager: TimeManager,
        id: Long,
    ): MessageWithData {

        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uris)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val imageFile = createImageFile(timeManager).toFile()

        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(imageFile)
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        withContext(Dispatchers.IO) {
            outputStream.close()
        }


        val path = imageFile.absolutePath
        val messageWithData = MessageWithData(
            id,
            path,
            creationTime = timeManager.getCreateTime(),
            messageType = MessageDataType.Image
        )
        insertMessageData(messageWithData)

        return messageWithData
    }


    fun createImageFile(timeManager: TimeManager): Path {
        val currentMoment = timeManager.nowLocalDateTime
        val timeStamp =
            "${currentMoment.year}${currentMoment.monthNumber}${currentMoment.dayOfMonth}_${currentMoment.hour}${currentMoment.minute}${currentMoment.second}"
        val imageFileName = "JPEG_" + timeStamp + "_"
        return kotlin.io.path.createTempFile(prefix = imageFileName, suffix = ".jpg")
    }


    override suspend fun deleteMessage(messageId: Long) {
        messageQueries.deleteMessage(messageId)
    }
}