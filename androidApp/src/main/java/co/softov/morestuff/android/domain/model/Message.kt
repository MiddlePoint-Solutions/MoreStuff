package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.MessageDataType
import co.softov.morestuff.android.domain.enums.ReplyType

data class Message(
    val id: Long,
    val taskId: Long = 0,
    val scheduleId: Long = 0,
    val contentType: ContentType,
    val createTime: String,
    val seenTime: String?,
    val content: String,
    val replyType: ReplyType?,
    val replyContent: String?,
    val replyTime: String?,
    val openGraphResult: OpenGraphResult?,
    val messageData: MessageData?
)

data class MessageWithFormattedTime(
    val message: Message,
    val formattedTime: String?
)

sealed class MessageData2 {

    object Empty : MessageData2()

    data class Image(
        val id: Long,
        val filePath: String,
        val creationTime: String,
        val messageType: MessageDataType
    ) : MessageData2()

}


