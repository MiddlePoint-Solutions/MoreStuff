package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter

typealias MessageData = co.softov.morestuff.db.Message
typealias MessageDbMapper = (MessageData) -> Message

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
    )
}







