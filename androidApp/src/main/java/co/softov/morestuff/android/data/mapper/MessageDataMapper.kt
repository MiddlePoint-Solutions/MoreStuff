package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

typealias MessageData = co.softov.morestuff.db.Message
typealias MessageDbMapper = (MessageData) -> Message

fun makeMessageDbMapper(): MessageDbMapper = { message ->
    mapMessageDb(message)
}

fun mapMessageDb(input: MessageData): Message {
    val createTime = formatTime(input.create_time)
    val seenTime = formatTime(input.seen_time)
    val replyTime = formatTime(input.reply_time)
    return Message(
        id = input.id,
        taskId = input.task_id,
        scheduleId = input.schedule_id,
        contentType = ContentType.withValue(input.content_type),
        createTime = createTime?: "",
        seenTime = seenTime,
        content = input.content,
        replyType = input.reply_type?.let { ReplyType.withValue(it) },
        replyContent = input.reply_content,
        replyTime = replyTime
    )
}

fun formatTime(timeString: String?): String? {
    return timeString?.let {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
        val localDateTime = ZonedDateTime.parse(it, formatter)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        localDateTime.format(timeFormatter)
    }
}





