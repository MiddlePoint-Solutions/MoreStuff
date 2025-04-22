package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.core.IAMessage

typealias IAMessageDbMapper = (
    id: Long,
    scope_id: Long,
    create_time: String,
    seen_time: String?,
    content_type: Int,
    content: String,
    reply_type: Int?,
    reply_content: String?,
    reply_time: String?
) -> IAMessage


fun makeIAMessageDbMapper(): IAMessageDbMapper = ::mapIAMessageDb

fun mapIAMessageDb(
    id: Long,
    scope_id: Long,
    create_time: String,
    seen_time: String?,
    content_type: Int,
    content: String,
    reply_type: Int?,
    reply_content: String?,
    reply_time: String?
): IAMessage {
    return IAMessage(
        id = id,
        scopeId = scope_id,
        contentType = ContentType.withValue(content_type),
        createTime = create_time,
        seenTime = seen_time,
        content = content,
        replyType = reply_type?.let { ReplyType.withValue(it) },
        replyContent = reply_content,
        replyTime = reply_time,
        openGraphResult = null,
        messageData = null
    )
}