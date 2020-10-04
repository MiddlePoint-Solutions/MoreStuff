package co.softov.morestuff.androidApp.data.mapper

import co.softov.morestuff.androidApp.domain.model.Message

typealias MessageDbMapper = (co.softov.morestuff.db.Message) -> Message

fun makeMessageDbMapper(): MessageDbMapper = { message ->
    mapMessageDb(message)
}

fun mapMessageDb(input: co.softov.morestuff.db.Message): Message {
    return Message(
        id = input.id,
        taskId = input.task_id,
        type = input.type,
        createTime = input.create_time,
        seenTime = input.seen_time,
        content = input.content,
        replyType = input.reply_type,
        replyContent = input.reply_content,
        replyTime = input.reply_time
    )
}