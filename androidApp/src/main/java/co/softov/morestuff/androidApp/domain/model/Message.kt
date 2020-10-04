package co.softov.morestuff.androidApp.domain.model

import co.softov.morestuff.androidApp.domain.enums.Message
import co.softov.morestuff.androidApp.domain.enums.ReplyType

data class Message(
    val id: Long,
    val taskId: Long,
    val type: Message,
    val createTime: Long,
    val seenTime: Long,
    val content: String,
    val replyType: ReplyType?,
    val replyContent: String?,
    val replyTime: Long
)