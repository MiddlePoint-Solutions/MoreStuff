package co.softov.morestuff.androidApp.domain.model

import co.softov.morestuff.androidApp.domain.enums.ContentType
import co.softov.morestuff.androidApp.domain.enums.ReplyType

data class Message(
    val id: Long,
    val taskId: Long,
    val contentType: ContentType,
    val createTime: String,
    val seenTime: String?,
    val content: String,
    val replyType: ReplyType?,
    val replyContent: String?,
    val replyTime: String?
)

