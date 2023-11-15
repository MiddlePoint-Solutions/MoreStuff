package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType

data class Message(
    val id: Long = 0,
    val taskId: Long = 0,
    val scheduleId: Long = 0,
    val contentType: ContentType,
    val createTime: String,
    val seenTime: String? = null,
    val content: String,
    val replyType: ReplyType? = null,
    val replyContent: String? = null,
    val replyTime: String? = null,
    val openGraphResult: OpenGraphResult? = null,
    val messageData: MessageData? = null
)
