@file:Suppress("LocalVariableName")

package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.data.utils.let4
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import kotlinx.serialization.json.Json

typealias MessageDb = co.softov.morestuff.db.Message
typealias MessageDbMapper = (MessageDb) -> Message

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

fun makeMessageWithDataMapper(): MessageDataMapper = ::mapMessageData

fun mapMessageData(
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
): Message {

    val openGraphResult = json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }

    val messageData = let4(
        message_data_id,
        message_data_file_path,
        message_data_creation_time,
        message_data_type?.let { MessageDataType.valueOf(it) },
        block = ::MessageData
    )

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


fun makeMessageDbMapper(timeFormatter: TimeFormatter): MessageDbMapper = { message ->
    mapMessageDb(message, timeFormatter)
}

fun mapMessageDb(input: MessageDb, timeFormatter: TimeFormatter): Message {
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
        messageData = null

    )
}







