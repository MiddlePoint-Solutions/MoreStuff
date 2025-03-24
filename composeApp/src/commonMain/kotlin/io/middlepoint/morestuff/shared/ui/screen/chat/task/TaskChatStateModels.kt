package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Immutable
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@Immutable
data class TaskChatState(
  val task: TaskUiModel = TaskUiModel(),
  val messages: List<MessageUiModel> = listOf()
)

@Immutable
sealed class TaskChatEvent {
  data class InputText(val content: String) : TaskChatEvent()
  data class InputUserMedia(val imageFile: PlatformFile, val title: String) : TaskChatEvent()
  data class InputDocument(val pdfFile: PlatformFile, val title: String) : TaskChatEvent()
  data class CopyText(val content: String) : TaskChatEvent()
  data class DeleteMessage(val message: MessageUiModel) : TaskChatEvent()
  data class ShareMessage(val message: MessageUiModel) : TaskChatEvent()
  data class ShareImage(val path: String) : TaskChatEvent()
  data class ShareDocument(val path: String) : TaskChatEvent()
  data class OpenDocument(val path: String) : TaskChatEvent()
  data class ScheduleResponse(val scheduleId: Long, val replyType: ReplyType) : TaskChatEvent()
  data object DeleteTask : TaskChatEvent()
  data object ToggleTaskComplete : TaskChatEvent()

}