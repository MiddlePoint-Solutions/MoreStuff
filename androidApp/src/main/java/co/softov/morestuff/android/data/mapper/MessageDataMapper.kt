package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message

typealias MessageData = co.softov.morestuff.db.Message
typealias MessageDbMapper = (MessageData) -> Message

fun makeMessageDbMapper(): MessageDbMapper = { message ->
    mapMessageDb(message)
}

fun mapMessageDb(input: MessageData): Message {
    return Message(
        id = input.id,
        taskId = input.task_id,
        scheduleId = input.schedule_id,
        contentType = ContentType.withValue(input.content_type),
        createTime = input.create_time,
        seenTime = input.seen_time,
        content = input.content,
        replyType = input.reply_type?.let { ReplyType.withValue(it) },
        replyContent = input.reply_content,
        replyTime = input.reply_time
    )
}