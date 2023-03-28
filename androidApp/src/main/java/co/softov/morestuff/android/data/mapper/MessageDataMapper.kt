package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.service.TimeManager

typealias MessageData = co.softov.morestuff.db.Message
typealias MessageDbMapper = (MessageData) -> Message

fun makeMessageDbMapper(timeManager: TimeManager): MessageDbMapper = { message ->
    mapMessageDb(message, timeManager)
}

fun mapMessageDb(input: MessageData, timeManager: TimeManager): Message {
    val createTime = timeManager.formatTime(input.create_time, "HH:mm")
    val seenTime = timeManager.formatTime(input.seen_time, "HH:mm")
    val replyTime = timeManager.formatTime(input.reply_time, "HH:mm")
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
        replyTime = replyTime
    )
}







