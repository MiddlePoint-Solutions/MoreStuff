package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.DevTools
import io.middlepoint.morestuff.shared.domain.enums.MessageExtraType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.MessageAction
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopeByTaskIdUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.shared.platform.ClipboardHelper
import io.middlepoint.morestuff.shared.platform.MediaHandler
import io.middlepoint.morestuff.shared.platform.ShareHelper
import io.middlepoint.morestuff.shared.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.ScopeUiMapper
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject

@Composable
fun taskChatModel(
  taskId: Uuid,
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

  val isAILoading by store.state
    .map { it.aiMessageState.loadingMap[taskId] ?: false }
    .collectAsState(initial = false)


  fun updateScopeAfterMove(scopeId: Uuid) {
    val newScope = allScopes.find { it.id == scopeId }
    newScope?.let {
      scope = scopeUiMapper.map(it)
      logger.d { "Scope updated to: ${it.name}" }
    }
  }

  LaunchedEffect(Unit) {
    getTaskFlow(taskId)
      .collect {
        logger.d { "Task flow ${taskId.value}" }
        task = taskUiMapper.map(it)
      }
  }

  LaunchedEffect(taskId) {
    getScopeByTaskIdUseCase(taskId).fold(
      { failure ->
        logger.e { "Error loading scope for task ${taskId.value}: $failure" }
      },
      { scopeDomain ->
        logger.d { "✅ Scope loaded for task ${taskId.value}: id=${scopeDomain.id.value}, name=${scopeDomain.name}" }
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
            when (message.messageExtra?.messageType) {
              MessageExtraType.Image -> {
                mediaHandler.shareImage(message.messageExtra.url)
              }

              MessageExtraType.Pdf -> {
                mediaHandler.sharePDF(message.messageExtra.url)
              }

              MessageExtraType.Video -> {}
              MessageExtraType.Audio -> {}
              null -> {
                shareHelper.shareMessage(message.content)
              }
            }
          }

          is ShareImage -> {
            mediaHandler.shareImage(path)
          }

          is ScheduleResponse -> {
            store.dispatch(NotificationAction.UserResponseAction(scheduleId, replyType))
          }

          is ShareDocument -> {
            mediaHandler.sharePDF(path)
          }

          is DeleteTask -> {
            store.dispatch(TaskAction.DeleteTasksAction(listOf(taskId)))
          }

          is ToggleTaskComplete -> {
            val complete = task?.isComplete == true
            store.dispatch(TaskAction.CompleteTasksAction(listOf(taskId), complete))
          }

          is SetEditingMessage -> {
            val previousEditingId = editingMessageId
            editingMessageId = messageId
            val message = messages.first { it.id == messageId }
            editingMessageContent = message.content

            // TODO: what is this and why do we need it?
            if (previousEditingId != null) {
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

          is CancelEditingMessage -> {
            editingMessageId = null
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

          is MoveTaskToScope -> {
            store.dispatch(
              TaskAction.UpdateTasksToScopeAction(listOf(taskId), scopeId)
            )
            updateScopeAfterMove(scopeId)
            val scopeTitle = allScopes.firstOrNull { it.id == scopeId }?.name ?: ""
            logger.d { "Task moved to scope: $scopeTitle" }
          }

          is CreateNewScopeForTask -> {
            createScopeUseCase(title).onRight { newScope ->
              store.dispatch(
                TaskAction.UpdateTasksToScopeAction(listOf(taskId), newScope.id)
              )

              logger.d { "Task moved to new scope: ${newScope.name}" }
            }
          }

          is CreateAIMessage -> {
            store.dispatch(
              MessageAction.CreateAITaskMessageAction(taskId, prompt)
            )
          }

          is ActivateAI -> {
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
    isAIEnabled = isAIEnabled,
    isAILoading = isAILoading,
  )
}
