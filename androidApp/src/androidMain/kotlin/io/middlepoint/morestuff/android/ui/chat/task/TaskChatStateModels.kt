package io.middlepoint.morestuff.android.ui.chat.task

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.TaskDomain
import io.middlepoint.morestuff.android.ui.model.MessageUiModel
import io.middlepoint.morestuff.android.ui.model.TaskUiModel
import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize

@Immutable
data class TaskChatState(
    val task: TaskUiModel = TaskUiModel(),
    val messages: List<MessageUiModel> = listOf()
)

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