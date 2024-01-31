package co.softov.morestuff.android.ui.chat.task


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.model.isOneTime
import co.softov.morestuff.android.domain.model.isReminder
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.ClipboardHelper
import co.softov.morestuff.android.domain.service.ImageHandler
import co.softov.morestuff.android.domain.service.PDFHandler
import co.softov.morestuff.android.domain.service.ShareTaskMessage
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import co.softov.morestuff.android.ui.model.map.MessageUiMapper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

class TaskChatViewModel(
    private val taskId: Long,
    private val clipboardHelper: ClipboardHelper,
    private val imageHandler: ImageHandler,
    private val pdfHandler: PDFHandler,
    private val timeFormatter: TimeFormatter,
    private val shareTaskMessage: ShareTaskMessage,
    private val messageUiMapper: MessageUiMapper,
    getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase,
    getTaskFlow: GetTaskFlowUseCase,
    devTools: DevTools,
) : NoStateViewModel() {

    init {
        Timber.d("TaskChatViewModel: $taskId")
    }

    val task: StateFlow<TaskDomain> = getTaskFlow(taskId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TaskDomain()
        )

    val messages: StateFlow<List<MessageUiModel>> = flow {
        while (true) {
            val messages = if (devTools.showDebugMessages) {
                getTaskMessagesFlowUseCase(taskId = taskId).first()
            } else {
                getTaskChatMessagesUseCase(taskId = taskId).first()
            }

            val formattedMessages = messageUiMapper.map(messages)

            emit(formattedMessages)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        dispatchAppStoreAction(UserResponseAction(scheduleId, replyType))
    }

    fun sendTaskChatMessage(content: String) {
        dispatchAppStoreAction(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

    fun sendImageMessageForTask(uris: String, message: String) {
        dispatchAppStoreAction(MessageAction.CreateImageMessageAction(taskId, uris, message))
    }

    fun sendPdfMessageForTask(uris: String, message: String) {
        dispatchAppStoreAction(MessageAction.CreatePDFMessageAction(taskId, uris, message))
    }

    fun copyToClipboard(text: String) {
        clipboardHelper.copyToClipboard(text)
    }

    fun deleteMessage(messageId: Long) {
        dispatchAppStoreAction(MessageAction.DeleteMessageAction(messageId))
    }

    fun shareImage(imagePath: String) {
        imageHandler.shareImage(imagePath)
    }
    fun sharePdf(pdfPath: String) {
        pdfHandler.sharePDF(pdfPath)
    }

    fun shareMessage(message: MessageUiModel) {
        shareTaskMessage.shareMessage(message)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
    fun formatCompleteTime(timeString: String?): String {
        return if (timeString != null) {
            timeFormatter.formatToDateTime(timeString) ?: "Format Error"
        } else {
            ""
        }
    }

    fun openPdf(pdfPath: String) {
        pdfHandler.openPDF(pdfPath)
    }
}
