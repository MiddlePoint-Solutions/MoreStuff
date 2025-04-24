@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.MediaFolder
import io.middlepoint.morestuff.shared.data.utils.let4
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.model.core.Message
import io.middlepoint.morestuff.shared.domain.model.MessageExtra
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

typealias MessageDataMapper = (
  id: Uuid,
  taskId: Uuid,
  scheduleId: Uuid?,
  created_at: Instant,
  updated_at: Instant,
  contentType: Int,
  content: String,
  deleted: Boolean,
  json_data: String?,
  message_data_id: Uuid?,
  message_data_url: String?,
  message_data_creation_time: Instant?,
  message_data_type: String?,
) -> Message

fun makeMessageDataMap(
  pathToSavedFile: (MediaFolder, String) -> String
) : MessageDataMapper = MessageDataMap(pathToSavedFile)

data class MessageDataMap(
  private val pathToSavedFile: (MediaFolder, String) -> String
) : MessageDataMapper{
  override fun invoke(
    id: Uuid,
    taskId: Uuid,
    scheduleId: Uuid?,
    created_at: Instant,
    updated_at: Instant,
    contentType: Int,
    content: String,
    deleted: Boolean,
    json_data: String?,
    message_data_id: Uuid?,
    message_data_url: String?,
    message_data_creation_time: Instant?,
    message_data_type: String?
  ): Message {

    val openGraphResult = json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }

    val messageExtra = let4(
      message_data_id,
      message_data_url,
      message_data_creation_time,
      message_data_type?.let { MessageExtraType.valueOf(it) },
    ) { dataId, dataPath, dataCreationTime, dataType ->
      MessageExtra(
        id = dataId,
        url = pathToSavedFile(dataType.toMediaFolder(), dataPath),
        creationTime = dataCreationTime.toString(),
        messageType = dataType
      )
    }

    return Message(
      id = id,
      taskId = taskId,
      scheduleId = scheduleId,
      createdAt = created_at,
      updatedAt = updated_at,
      contentType = ContentType.withValue(contentType),
      content = content,
      deleted = deleted,
      openGraphResult = openGraphResult,
      messageExtra = messageExtra
    )
  }
}

private fun MessageExtraType.toMediaFolder() : MediaFolder = when(this) {
  MessageExtraType.Image -> MediaFolder.Images
  MessageExtraType.Video -> TODO()
  MessageExtraType.Audio -> TODO()
  MessageExtraType.Pdf -> MediaFolder.Files
}

