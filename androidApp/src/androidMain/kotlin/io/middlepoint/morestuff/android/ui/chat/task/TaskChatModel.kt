package io.middlepoint.morestuff.android.ui.chat.task

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
import io.middlepoint.morestuff.shared.domain.service.ClipboardHelper
import io.middlepoint.morestuff.shared.domain.service.ImageHandler
import io.middlepoint.morestuff.shared.domain.service.PDFHandler
import io.middlepoint.morestuff.shared.domain.service.ShareTaskMessage
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskChatMessagesUseCase
import io.middlepoint.morestuff.shared.domain.usecase.message.GetTaskMessagesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.android.ui.chat.task.TaskChatEvent.*
import io.middlepoint.morestuff.android.ui.model.map.MessageUiMapper
import io.middlepoint.morestuff.android.ui.model.map.TaskUiMapper
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
    imageHandler: ImageHandler = koinInject(),
    pdfHandler: PDFHandler = koinInject(),
    taskUiMapper: TaskUiMapper = koinInject(),
    shareTaskMessage: ShareTaskMessage = koinInject(),
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
            .map(taskUiMapper::map)
            .collect { task = it }
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
        logger.d { "Inside LaunchedEffect" }
        events.collect { event ->
            logger.d { "Inside Collect: $event" }
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
                            MessageAction.CreatePDFMessageAction(taskId, path, title.trim())
                        )
                    }

                    is InputImage -> {
                        store.dispatch(
                            MessageAction.CreateImageMessageAction(taskId, path, title.trim())
                        )
                    }

                    is InputText -> {
                        store.dispatch(
                            MessageAction.CreateUserTaskMessageAction(taskId, content.trim())
                        )
                    }

                    is OpenDocument -> {
                        pdfHandler.openPDF(path)
                    }

                    is ShareMessage -> {
                        when (message.messageData?.messageType) {
                            MessageDataType.Image -> {
                                imageHandler.shareImage(message.messageData.filePath)
                            }

                            MessageDataType.Pdf -> {
                                pdfHandler.sharePDF(message.messageData.filePath)
                            }

                            MessageDataType.Video -> {}
                            MessageDataType.Audio -> {}
                            null -> {
                                shareTaskMessage.shareMessage(message.content)
                            }
                        }
                    }

                    is ShareImage -> {
                        imageHandler.shareImage(path)
                    }

                    is ScheduleResponse -> {
                        store.dispatch(ReminderAction.UserResponseAction(scheduleId, replyType))
                    }

                    is ShareDocument -> {
                        pdfHandler.sharePDF(path)
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
