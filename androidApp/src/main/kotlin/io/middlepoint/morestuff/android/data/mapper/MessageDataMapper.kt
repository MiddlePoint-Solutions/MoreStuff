@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.android.data.mapper

import io.middlepoint.morestuff.android.data.utils.let4
import io.middlepoint.morestuff.android.domain.enums.ContentType
import io.middlepoint.morestuff.android.domain.enums.MessageDataType
import io.middlepoint.morestuff.android.domain.enums.ReplyType
import io.middlepoint.morestuff.android.domain.model.Message
import io.middlepoint.morestuff.android.domain.model.MessageData
import io.middlepoint.morestuff.android.domain.model.OpenGraphResult
import kotlinx.serialization.json.Json

typealias MessageDb = io.middlepoint.morestuff.db.Message
typealias MessageDbMapper = (
    id: Long,
    task_id: Long,
    schedule_id: Long,
    create_time: String,
    seen_time: String?,
    content_type: Int,
    content: String,
    reply_type: Int?,
    reply_content: String?,
    reply_time: String?,
) -> Message

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
    json_data: String? = null,
    message_data_id: Long? = null,
    message_data_file_path: String? = null,
    message_data_creation_time: String? = null,
    message_data_type: String? = null,
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


fun makeMessageDbMapper(): MessageDbMapper = ::mapMessageDb

fun mapMessageDb(
    id: Long,
    task_id: Long,
    schedule_id: Long,
    create_time: String,
    seen_time: String?,
    content_type: Int,
    content: String,
    reply_type: Int?,
    reply_content: String?,
    reply_time: String?,
): Message {
    return Message(
        id = id,
        taskId = task_id,
        scheduleId = schedule_id,
        contentType = ContentType.withValue(content_type),
        createTime = create_time,
        seenTime = seen_time,
        content = content,
        replyType = reply_type?.let { ReplyType.withValue(it) },
        replyContent = reply_content,
        replyTime = reply_time,
        openGraphResult = null,
        messageData = null
    )
}







