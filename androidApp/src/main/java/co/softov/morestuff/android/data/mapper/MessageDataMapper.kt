package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.data.utils.let4
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageWithData
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import kotlinx.serialization.json.Json

typealias MessageData = co.softov.morestuff.db.Message
typealias MessageDbMapper = (MessageData) -> Message

typealias ImageMessageDataMapper = (
    id: Long,
    taskId: Long,
    scheduleId: Long,
    contentType: ContentType,//Cambiar a string?
    createTime: String,
    seenTime: String?,
    content: String,
    replyType: ReplyType?,//Cambiar a string
    replyContent: String?,
    replyTime: String?,
    json_data: String?,
    message_with_data_id: Long?,
    message_with_data_file_path: String?,
    message_with_data_creation_time: String?,
    message_with_data_message_type: MessageDataType?,//cambiar a string?
) -> Message

fun makeMessageWithDataMapper(): ImageMessageDataMapper = ::mapMessageData
fun mapMessageData(
    id: Long,
    taskId: Long,
    scheduleId: Long,
    contentType: ContentType,
    createTime: String,
    seenTime: String?,
    content: String,
    replyType: ReplyType?,
    replyContent: String?,
    replyTime: String?,
    json_data: String?,
    message_with_data_id: Long?,
    message_with_data_file_path: String?,
    message_with_data_creation_time: String?,
    message_with_data_message_type: MessageDataType?,
): Message {

    val openGraphResult = json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }

    val messageWithData = let4(
        message_with_data_id,
        message_with_data_file_path,
        message_with_data_creation_time,
        message_with_data_message_type,
        block = ::MessageWithData
    )
    return Message(
        id = id,
        taskId = taskId,
        scheduleId = scheduleId,
        contentType = contentType,
        createTime = createTime,
        seenTime = seenTime,
        content = content,
        replyType = replyType,
        replyContent = replyContent,
        replyTime = replyTime,
        openGraphResult = openGraphResult,
        messageWithData = messageWithData
    )
}


fun makeMessageDbMapper(timeFormatter: TimeFormatter): MessageDbMapper = { message ->
    mapMessageDb(message, timeFormatter)
}

fun mapMessageDb(input: MessageData, timeFormatter: TimeFormatter): Message {
    val createTime = timeFormatter.formatTimeOnly(input.create_time)
    val seenTime = timeFormatter.formatTimeOnly(input.seen_time)
    val replyTime = timeFormatter.formatTimeOnly(input.reply_time)
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
        openGraphResult = null,
        messageWithData = null

    )
}







