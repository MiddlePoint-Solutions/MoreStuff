@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.MediaFolder
import io.middlepoint.morestuff.shared.StorageManager
import io.middlepoint.morestuff.shared.data.utils.let4
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import kotlinx.serialization.json.Json

typealias MessageDataMapper = (
  id: Long,
  taskId: Long,
  scheduleId: Long,
  createTime: String,
  seenTime: String?,
  contentType: Int,
  content: String,
  replyType: Int?,
  replyContent: String?,
  replyTime: String?,
  json_data: String?,
  message_data_id: Long?,
  message_data_file_path: String?,
  message_data_creation_time: String?,
  message_data_type: String?,
) -> Message

fun makeMessageDataMap(
  pathToSavedFile: (MediaFolder, String) -> String
) : MessageDataMapper = MessageDataMap(pathToSavedFile)

data class MessageDataMap(
  private val pathToSavedFile: (MediaFolder, String) -> String
) : MessageDataMapper {
  override fun invoke(
    id: Long,
    taskId: Long,
    scheduleId: Long,
    createTime: String,
    seenTime: String?,
    contentType: Int,
    content: String,
    replyType: Int?,
    replyContent: String?,
    replyTime: String?,
    json_data: String?,
    message_data_id: Long?,
    message_data_file_path: String?,
    message_data_creation_time: String?,
    message_data_type: String?
  ): Message {

    val openGraphResult = json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }

    val messageData = let4(
      message_data_id,
      message_data_file_path,
      message_data_creation_time,
      message_data_type?.let { MessageDataType.valueOf(it) },
    ) { dataId, dataPath, dataCreationTime, dataType ->
      MessageData(
        id = dataId,
        filePath = pathToSavedFile(dataType.toMediaFolder(), dataPath),
        creationTime = dataCreationTime,
        messageType = dataType
      )
    }

    return Message(
      id = id,
      taskId = taskId,
      scheduleId = scheduleId,
      contentType = ContentType.withValue(contentType),
      createTime = createTime,
      seenTime = seenTime,
      content = content,
      replyType = replyType?.let { ReplyType.withValue(it) },
      replyContent = replyContent,
      replyTime = replyTime,
      openGraphResult = openGraphResult,
      messageData = messageData
    )
  }
}

private fun MessageDataType.toMediaFolder() : MediaFolder = when(this) {
  MessageDataType.Image -> MediaFolder.Images
  MessageDataType.Video -> TODO()
  MessageDataType.Audio -> TODO()
  MessageDataType.Pdf -> MediaFolder.Files
}

