package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.ContentType
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

