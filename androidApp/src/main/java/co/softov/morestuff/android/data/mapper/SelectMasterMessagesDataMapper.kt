package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.chat.items.OpenGraphResult
import co.softov.morestuff.db.SelectMasterMessages
import kotlinx.serialization.json.Json

typealias SelectMasterMessagesMapper = (SelectMasterMessages) -> Message

fun SelectMasterMessagesMapper(timeFormatter: TimeFormatter): (SelectMasterMessages) -> Message = { SelectMasterMessages ->
    mapSelectMasterMessagesToMessageData(SelectMasterMessages, timeFormatter)
}

fun mapSelectMasterMessagesToMessageData(input: SelectMasterMessages, timeFormatter: TimeFormatter): Message {
    val createTime = timeFormatter.formatTimeOnly(input.create_time)
    val seenTime = timeFormatter.formatTimeOnly(input.seen_time)
    val replyTime = timeFormatter.formatTimeOnly(input.reply_time)
    val openGraphResult = input.json_data?.let { Json.decodeFromString<OpenGraphResult>(it) }
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
        imagePath = null
    )
}