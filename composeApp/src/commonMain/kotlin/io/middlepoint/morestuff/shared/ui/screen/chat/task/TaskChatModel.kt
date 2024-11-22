package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.MessageAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ReminderAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.TaskAction
import io.middlepoint.morestuff.shared.ClipboardHelper
import io.middlepoint.morestuff.shared.MediaHandler
import io.middlepoint.morestuff.shared.ShareHelper
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.shared.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CopyText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteTask
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.OpenDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ScheduleResponse
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareImage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ToggleTaskComplete
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun taskChatModel(
  taskId: Long,
  initialState: TaskChatState,
  events: Flow<TaskChatEvent>,
  store: AppStore = koinInject(),
  clipboardHelper: ClipboardHelper = koinInject(),
  mediaHandler: MediaHandler = koinInject(),
  taskUiMapper: TaskUiMapper = koinInject(),
  shareHelper: ShareHelper = koinInject(),
  messageUiMapper: MessageUiMapper = koinInject(),
  getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase = koinInject(),
  getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase = koinInject(),
  getTaskFlow: GetTaskFlowUseCase = koinInject(),
  devTools: DevTools = koinInject(),
  logger: Logger = koinInject()
): TaskChatState {

  var task by remember { mutableStateOf(initialState.task) }
  var messages by remember { mutableStateOf(initialState.messages) }

  LaunchedEffect(Unit) {
    getTaskFlow(taskId)
      .collect {
        logger.d { "Task flow" }
        task = taskUiMapper.map(it)
      }
  }

  LaunchedEffect(Unit) {
    val messagesFlow = when {
      devTools.showDebugMessages -> getTaskMessagesFlowUseCase(taskId = taskId)
      else -> getTaskChatMessagesUseCase(taskId = taskId)
    }
    messagesFlow
      .map(messageUiMapper::map)
      .collect { messages = it }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      with(event) {
        when (this) {
          is CopyText -> {
            clipboardHelper.copyToClipboard(content)
          }

          is DeleteMessage -> {
            store.dispatch(MessageAction.DeleteMessageAction(message.id))
          }

          is InputDocument -> {
            store.dispatch(
              MessageAction.CreatePDFMessageAction(taskId, pdfFile, title.trim())
            )
          }

          is TaskChatEvent.InputUserMedia -> {
            store.dispatch(
              MessageAction.CreateFileMessageAction(taskId, imageFile, title.trim())
            )
          }

          is InputText -> {
            store.dispatch(
              MessageAction.CreateUserTaskMessageAction(taskId, content.trim())
            )
          }

          is OpenDocument -> {
            mediaHandler.openPDF(path)
          }

          is ShareMessage -> {
            when (message.messageData?.messageType) {
              MessageDataType.Image -> {
                mediaHandler.shareImage(message.messageData.filePath)
              }

              MessageDataType.Pdf -> {
                mediaHandler.sharePDF(message.messageData.filePath)
              }

              MessageDataType.Video -> {}
              MessageDataType.Audio -> {}
              null -> {
                shareHelper.shareMessage(message.content)
              }
            }
          }

          is ShareImage -> {
            mediaHandler.shareImage(path)
          }

          is ScheduleResponse -> {
            store.dispatch(ReminderAction.UserResponseAction(scheduleId, replyType))
          }

          is ShareDocument -> {
            mediaHandler.sharePDF(path)
          }

          is DeleteTask -> {
            store.dispatch(TaskAction.DeleteTasksAction(listOf(taskId)))
          }

          is ToggleTaskComplete -> {
            val complete = !task.isComplete
            store.dispatch(TaskAction.CompleteTasksAction(listOf(taskId), complete))
          }
        }
      }
    }
  }

  return TaskChatState(
    task = task,
    messages = messages
  )
}
