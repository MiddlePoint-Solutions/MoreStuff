package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.db.SelectTaskMessagesByContentType

typealias SelectTaskMessagesByContentTypeMapper = (SelectTaskMessagesByContentType) -> MessageData

fun makeSelectTaskMessagesByContentTypeMapper(): SelectTaskMessagesByContentTypeMapper = { selectTaskMessagesByContentType ->
    mapSelectTaskMessagesByContentTypeToMessageData(selectTaskMessagesByContentType)
}

fun mapSelectTaskMessagesByContentTypeToMessageData(input: SelectTaskMessagesByContentType): MessageData {
    return MessageData(
        id = input.id,
        task_id = input.task_id,
        schedule_id = input.schedule_id,
        content_type = input.content_type,
        create_time = input.create_time,
        seen_time = input.seen_time,
        content = input.content,
        reply_type = input.reply_type,
        reply_content = input.reply_content,
        reply_time = input.reply_time,
    )
}