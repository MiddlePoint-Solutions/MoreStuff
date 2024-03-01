package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.TaskUiModel
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class TaskChatState(
    val task: TaskUiModel = TaskUiModel(),
    val messages: List<MessageUiModel> = listOf()
) : Parcelable

@Immutable
sealed class TaskChatEvent {
    data class InputText(val content: String) : TaskChatEvent()
    data class InputImage(val path: String, val title: String) : TaskChatEvent()
    data class InputDocument(val path: String, val title: String) : TaskChatEvent()
    data class CopyText(val content: String) : TaskChatEvent()
    data class DeleteMessage(val message: MessageUiModel) : TaskChatEvent()
    data class ShareMessage(val message: MessageUiModel) : TaskChatEvent()
    data class ShareImage(val path: String) : TaskChatEvent()
    data class ShareDocument(val path: String) : TaskChatEvent()
    data class OpenDocument(val path: String) : TaskChatEvent()
    data class ScheduleResponse(val scheduleId: Long, val replyType: ReplyType) : TaskChatEvent()
    data object DeleteTask : TaskChatEvent()
    data object ToggleTaskComplete: TaskChatEvent()

}