package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.model.MessageUiModel
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class ChatState(
    val task: TaskDomain = TaskDomain(),
    val completedTime: String? = null,
    val messages: List<MessageUiModel> = listOf()
) : Parcelable

@Immutable
sealed class ChatEvent {
    data class InputText(val content: String) : ChatEvent()
    data class InputImage(val path: String, val title: String) : ChatEvent()
    data class InputDocument(val path: String, val title: String) : ChatEvent()
    data class CopyText(val content: String) : ChatEvent()
    data class DeleteMessage(val message: MessageUiModel) : ChatEvent()
    data class ShareMessage(val message: MessageUiModel) : ChatEvent()
    data class ShareImage(val path: String) : ChatEvent()
    data class ShareDocument(val path: String) : ChatEvent()
    data class OpenDocument(val path: String) : ChatEvent()
    data class ScheduleResponse(val scheduleId: Long, val replyType: ReplyType) : ChatEvent()

}