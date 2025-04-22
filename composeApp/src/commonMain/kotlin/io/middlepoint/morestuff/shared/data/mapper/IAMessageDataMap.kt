package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.MediaFolder
import io.middlepoint.morestuff.shared.StorageManager
import io.middlepoint.morestuff.shared.data.utils.let4
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.MessageData
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage
import kotlinx.serialization.json.Json


typealias IAMessageDataMapper = (
    id: Long,
    scopeId: Long,
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
) -> IAMessage


data class IAMessageDataMap(
    private val storageManager: StorageManager
) : IAMessageDataMapper {
    override fun invoke(
        id: Long,
        scopeId: Long,
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
    ): IAMessage {

        val openGraphResult = json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }

        val messageData = let4(
            message_data_id,
            message_data_file_path,
            message_data_creation_time,
            message_data_type?.let { MessageDataType.valueOf(it) },
        ) { dataId, dataPath, dataCreationTime, dataType ->
            MessageData(
                id = dataId,
                filePath = storageManager.getAppStoragePathToSavedFile(dataType.toMediaFolder(), dataPath),
                creationTime = dataCreationTime,
                messageType = dataType
            )
        }

        return IAMessage(
            id = id,
            scopeId = scopeId,
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

private fun MessageDataType.toMediaFolder(): MediaFolder = when (this) {
    MessageDataType.Image -> MediaFolder.Images
    MessageDataType.Video -> TODO()
    MessageDataType.Audio -> TODO()
    MessageDataType.Pdf -> MediaFolder.Files
}