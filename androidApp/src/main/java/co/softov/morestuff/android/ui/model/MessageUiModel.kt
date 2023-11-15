package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.model.OpenGraphResult

@Immutable
data class MessageUiModel(
    val id: Long = 0,
    val taskId: Long = 0,
    val scheduleId: Long = 0,
    val contentType: ContentType,
    val createTime: String,
    val content: String,
    val replyType: ReplyType? = null,
    val replyContent: String? = null,
    val openGraphResult: OpenGraphResult? = null,
    val messageData: MessageData? = null,
    val formattedTime: String,
    val formattedTimeOnly: String
){
    val isDataMessage: Boolean get() = messageData != null
}