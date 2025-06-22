package io.middlepoint.morestuff.shared.data.repository


import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Either
import arrow.core.Either.*
import arrow.core.left
import arrow.core.right
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.shared.data.mapper.DataMappers
import io.middlepoint.morestuff.shared.data.mapper.MessageData
import io.middlepoint.morestuff.shared.data.mapper.MessageExtraData
import io.middlepoint.morestuff.shared.data.utils.generate
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.repository.MessageDoesNotExist
import io.middlepoint.morestuff.shared.domain.repository.MessageRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class MessageRepositoryImpl(
  database: StuffDb,
  private val mapper: DataMappers,
  private val timeManager: TimeManager,
) : MessageRepository {

  private val messageQueries = database.messagesQueries
  private val urlMetadataQueries = database.urlMetadataQueries
  private val messageDataQueries = database.messagesExtraQueries

  override fun getTaskMessagesFlow(taskId: Uuid): Flow<List<Message>> =
    messageQueries.selectMessageByTaskId(taskId, mapper = mapper.messageDataMapper)
      .asFlow()
      .mapToList(Dispatchers.Default)

  override suspend fun getTaskChatMessages(taskId: Uuid): List<Message> =
    messageQueries.selectTaskMessagesByContentType(
      taskId,
      listOf(ContentType.TASK_MESSAGE.value, ContentType.APP_TASK_MESSAGE.value, ContentType.AI_TASK_MESSAGE.value),
      mapper = mapper.messageDataMapper
    ).awaitAsList()

  override fun getTaskChatMessagesFlow(taskId: Uuid): Flow<List<Message>> =
    messageQueries.selectTaskMessagesByContentType(
      taskId,
      listOf(ContentType.TASK_MESSAGE.value, ContentType.APP_TASK_MESSAGE.value, ContentType.AI_TASK_MESSAGE.value),
      mapper = mapper.messageDataMapper
    ).asFlow().mapToList(Dispatchers.Default)

  override suspend fun getMessage(messageId: Uuid): Either<Failure, Message> {
    val message = messageQueries.selectMessageById(
      id = messageId,
      mapper = mapper.messageDataMapper
    ).awaitAsOneOrNull()
    return when (message) {
      null -> MessageDoesNotExist.left()
      else -> message.right()
    }
  }

  override suspend fun createMessage(
    taskId: Uuid,
    scheduleId: Uuid?,
    contentType: Int,
    legacyCreatedAt: String?,
    messageExtra: MessageExtra?,
    content: String,
  ): Either<Failure, Message> = messageQueries.transactionWithResult {
    val createdAt = (legacyCreatedAt?.let(Instant::parse) ?: timeManager.nowUtcInstant)
    val messageData = MessageData(
      id = Uuid.generate(),
      task_id = taskId,
      schedule_id = scheduleId,
      created_at = createdAt,
      updated_at = createdAt,
      content_type = contentType,
      content = content,
      deleted = false
    )
    messageQueries.insertMessage(messageData)
    // TODO: message extra should be created before the message.
    messageExtra?.let {
      val messageExtraData = MessageExtraData(
        id = Uuid.generate(),
        message_id = messageData.id,
        url = messageExtra.url,
        created_at = createdAt,
        updated_at = createdAt,
        data_type = messageExtra.messageType.name,
        deleted = false
      )
      messageDataQueries.insertMessageExtra(messageExtraData)
    }
    messageQueries.selectMessageById(
      id = messageData.id,
      mapper = mapper.messageDataMapper
    ).awaitAsOne().right()
  }

  override suspend fun insertUrlMetadata(
    url: String,
    openGraphResult: OpenGraphResult,
    messageId: Uuid,
  ) {
    val openGraphResultJson = Json.encodeToString(openGraphResult)
    urlMetadataQueries.insertUrlMetadata(
      url = url,
      json_data = openGraphResultJson,
      message_id = messageId
    )
  }

  override suspend fun deleteMessage(messageId: Uuid) {
    val message = messageQueries.selectMessageById(
      id = messageId,
      mapper = mapper.messageDataMapper
    ).awaitAsOneOrNull()

    val imagePath = message?.messageExtra?.url
    imagePath?.let {
      // TODO
//            val file = File(it)
//            if (file.exists()) {
//                file.delete()
//            }
    }
    messageQueries.deleteMessage(messageId)
  }


  override suspend fun updateMessageContent(
    messageId: Uuid,
    content: String,
  ): Either<Failure, Boolean> {
    messageQueries.updateMessageContent(content, messageId)
    return Right(true)
  }

}