package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Immutable
import io.github.vinceglb.filekit.PlatformFile
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.ScopeUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@Immutable
data class TaskChatState(
  val task: TaskUiModel = TaskUiModel(),
  val scope: ScopeUiModel = ScopeUiModel(),
  val messages: List<MessageUiModel> = listOf(),
  val editingMessageId: Uuid? = null,
  val editingMessageContent: String = "",
  val allScopes: List<Scope> = listOf(),
  val isAIEnabled: Boolean = false,
  val isAILoading: Boolean = false,
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
  data class ScheduleResponse(val scheduleId: Uuid, val replyType: ReplyType) : TaskChatEvent()
  data object DeleteTask : TaskChatEvent()
  data object ToggleTaskComplete : TaskChatEvent()
  data class SetEditingMessage(val messageId: Uuid) : TaskChatEvent()
  data object CancelEditingMessage : TaskChatEvent()
  data class UpdateMessageContent(val content: String) : TaskChatEvent()
  data class CreateTaskCompletionMessage(val content: String) : TaskChatEvent()
  data class MoveTaskToScope(val scopeId: Uuid) : TaskChatEvent()
  data class CreateNewScopeForTask(val title: String) : TaskChatEvent()
  data object ActivateAI : TaskChatEvent()
  data class CreateAIMessage(val prompt: String) : TaskChatEvent()
}