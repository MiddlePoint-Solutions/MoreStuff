package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.ClipboardHelper
import io.middlepoint.morestuff.shared.MediaHandler
import io.middlepoint.morestuff.shared.ShareHelper
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.enums.MessageDataType
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.MessageAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ReminderAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.TaskAction
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopeByTaskIdUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.shared.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.ScopeUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CopyText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CreateTaskCompletionMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteTask
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputUserMedia
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.OpenDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ScheduleResponse
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.SetEditingMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareImage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ToggleTaskComplete
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.UpdateMessageContent
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
  scopeUiMapper: ScopeUiMapper = koinInject(),
  shareHelper: ShareHelper = koinInject(),
  messageUiMapper: MessageUiMapper = koinInject(),
  getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase = koinInject(),
  getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase = koinInject(),
  getTaskFlow: GetTaskFlowUseCase = koinInject(),
  getScopeByTaskIdUseCase: GetScopeByTaskIdUseCase = koinInject(),
  getScopesFlowUseCase: GetScopesFlowUseCase = koinInject(),
  createScopeUseCase: CreateScopeUseCase = koinInject(),
  devTools: DevTools = koinInject(),
  logger: Logger = koinInject()
): TaskChatState {

  var task by remember { mutableStateOf(initialState.task) }
  var scope by remember { mutableStateOf(initialState.scope) }
  var messages by remember { mutableStateOf(initialState.messages) }
  var editingMessageId by remember { mutableStateOf(initialState.editingMessageId) }
  var editingMessageContent by remember { mutableStateOf(initialState.editingMessageContent) }
  var allScopes by remember { mutableStateOf(initialState.allScopes) }
  var isAIEnabled by remember { mutableStateOf(initialState.isAIEnabled) }

  fun updateScopeAfterMove(scopeId: Long) {
    val newScope = allScopes.find { it.id == scopeId }
    newScope?.let {
      scope = scopeUiMapper.map(it)
      logger.d { "Scope updated to: ${it.name}" }
    }
  }

  LaunchedEffect(Unit) {
    getTaskFlow(taskId)
      .collect {
        logger.d { "Task flow" }
        task = taskUiMapper.map(it)
      }
  }

  LaunchedEffect(taskId) {
    getScopeByTaskIdUseCase(taskId).fold(
      { failure ->
        logger.e { "Error loading scope for task: $failure" }
      },
      { scopeDomain ->
        scope = scopeUiMapper.map(scopeDomain)
      }
    )
  }

  LaunchedEffect(Unit) {
    getScopesFlowUseCase().collect {
      logger.d { "Scopes updated: ${it.size} scopes received" }
      allScopes = it
    }
  }

  LaunchedEffect(Unit) {
    val messagesFlow = when {
      devTools.showDebugMessages -> getTaskMessagesFlowUseCase(taskId = taskId)
      else -> getTaskChatMessagesUseCase(taskId = taskId).asFlow()
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

          is InputUserMedia -> {
            store.dispatch(
              MessageAction.CreateFileMessageAction(taskId, imageFile, title.trim())
            )
          }

          /*is InputText -> {
            store.dispatch(
              MessageAction.CreateUserTaskMessageAction(taskId, content.trim())
            )
            store.dispatch(
              MessageAction.CreateAITaskMessageAction(taskId, content.trim())
            )


          }*/

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

          is SetEditingMessage -> {
            val previousEditingId = editingMessageId
            editingMessageId = messageId

            if (messageId > 0) {
              val message = messages.find { it.id == messageId }
              message?.let {
                editingMessageContent = it.content
              }
            } else {
              if (previousEditingId != null && previousEditingId > 0) {
                if (editingMessageContent.isNotEmpty()) {
                  store.dispatch(
                    MessageAction.UpdateMessageContentAction(
                      previousEditingId,
                      editingMessageContent.trim()
                    )
                  )
                }
              }
              editingMessageContent = ""
            }
          }

          is UpdateMessageContent -> {
            editingMessageContent = content
          }

          is CreateTaskCompletionMessage -> {
            store.dispatch(
              MessageAction.CreateAppTaskMessageAction(
                taskId = taskId,
                content = content,
              )
            )
          }

          is TaskChatEvent.MoveTaskToScope -> {
            store.dispatch(
              TaskAction.UpdateTasksToScopeAction(listOf(taskId), scopeId)
            )
            updateScopeAfterMove(scopeId)
            val scopeTitle = allScopes.firstOrNull { it.id == scopeId }?.name ?: ""
            logger.d { "Task moved to scope: $scopeTitle" }
          }

          is TaskChatEvent.CreateNewScopeForTask -> {
            createScopeUseCase(title).onRight { newScope ->
              store.dispatch(
                TaskAction.UpdateTasksToScopeAction(listOf(taskId), newScope.id)
              )

              logger.d { "Task moved to new scope: ${newScope.name}" }
            }
          }

          is TaskChatEvent.CreateAIMessage -> {
            store.dispatch(
              MessageAction.CreateAITaskMessageAction(taskId, prompt)
            )
          }

          is TaskChatEvent.ActivateAI -> {
            isAIEnabled = !isAIEnabled
            logger.d { "AI ${if (isAIEnabled) "enabled" else "disabled"} for task: $taskId" }
          }
        }
      }
    }
  }

  return TaskChatState(
    task = task,
    scope = scope,
    messages = messages,
    editingMessageId = editingMessageId,
    editingMessageContent = editingMessageContent,
    allScopes = allScopes,
    isAIEnabled = isAIEnabled
  )
}
