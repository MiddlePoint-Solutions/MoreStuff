package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.db.SelectTaskMessagesByContentType
import kotlinx.serialization.json.Json
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageWithData

typealias SelectTaskMessagesByContentTypeMapper = (SelectTaskMessagesByContentType) -> Message

fun makeSelectTaskMessagesByContentTypeMapper(timeFormatter: TimeFormatter): (SelectTaskMessagesByContentType) -> Message = { selectTaskMessagesByContentType ->
    mapSelectTaskMessagesByContentTypeToMessageData(selectTaskMessagesByContentType, timeFormatter, )
}

fun mapSelectTaskMessagesByContentTypeToMessageData(input: SelectTaskMessagesByContentType, timeFormatter: TimeFormatter): Message {
    val createTime = timeFormatter.formatTimeOnly(input.create_time)
    val seenTime = timeFormatter.formatTimeOnly(input.seen_time)
    val replyTime = timeFormatter.formatTimeOnly(input.reply_time)
    val openGraphResult = input.json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }
    val messageWithData = if (input.file_path != null) {
        MessageWithData(
            id = input.id ,
            filePath = input.file_path,
            creationTime = createTime?: "",
            messageType = MessageDataType.Image
        )
    } else {
        null
    }

    return Message(
        id = input.id,
        taskId = input.task_id,
        scheduleId = input.schedule_id,
        contentType = ContentType.withValue(input.content_type),
        createTime = createTime ?: "",
        seenTime = seenTime,
        content = input.content,
        replyType = input.reply_type?.let { ReplyType.withValue(it) },
        replyContent = input.reply_content,
        replyTime = replyTime,
        openGraphResult = openGraphResult,
        messageWithData = messageWithData
    )
}