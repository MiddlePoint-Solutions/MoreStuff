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
    private val timeManager: TimeManager,
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
        .onEach { task ->
            scheduleModel = createModelForSchedule(task.schedule.firstOrNull { it.isOneTime() })
            reminderModel = createModelForSchedule(task.schedule.firstOrNull { it.isReminder() })
        }
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

    var scheduleModel by mutableStateOf<ScheduleUiModel?>(null)
        private set

    var reminderModel by mutableStateOf<ScheduleUiModel?>(null)
        private set

    var taskTitle by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            taskTitle = getTaskFlow(taskId = taskId).first().title
        }
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        dispatchAppStoreAction(UserResponseAction(scheduleId, replyType))
    }

    fun updateTaskTitle(title: String) {
        taskTitle = title
        if (title.isNotEmpty()) {
            dispatchAppStoreAction(TaskAction.UpdateTaskTitleAction(taskId, title))
        }
    }

    fun toggleTaskComplete() {
        dispatchAppStoreAction(
            TaskAction.CompleteTasksAction(
                taskIds = listOf(taskId),
                !task.value.isComplete
            )
        )
    }

    fun sendTaskChatMessage(content: String) {
        dispatchAppStoreAction(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

    fun sendImageMessageForTask(uris: String, message: String) {
        dispatchAppStoreAction(MessageAction.CreateImageMessageAction(taskId, uris, message))
    }

    fun sendPdfMessageForTask(uris: String, message: String) {
        dispatchAppStoreAction(MessageAction.CreatePdfMessageAction(taskId, uris, message))
    }

    fun createOneTimeSchedule() {
        createScheduleModel().run {
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, localDateTime)
            )
        }
    }

    fun updatePlanTime(hour: Int, minute: Int) {
        scheduleModel?.let {
            val updatedPlanTime = timeManager.localDateTime(it.localDateTime, hour, minute)
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, updatedPlanTime)
            )
        }
    }

    fun updatePlanDate(dateMillis: Long) {
        scheduleModel?.let {
            val updatedPlanTime =
                timeManager.epochMillisToLocalDateTime(dateMillis, it.hour, it.minute)
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, updatedPlanTime)
            )
        }
    }

    fun cancelActiveSchedule() {
        dispatchAppStoreAction(ScheduleAction.CancelActiveScheduleAction(taskId))
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

    private fun createModelForSchedule(scheduleDomain: ScheduleDomain?) =
        scheduleDomain?.scheduleLocalTime?.let {
            val localTime = scheduleDomain.scheduleLocalTime.toLocalDateTime()
            createScheduleModel(localTime)
        }


    private fun createScheduleModel(
        time: LocalDateTime = timeManager.getDefaultPlanTime(),
    ) = ScheduleUiModel(
        localDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "Error"
    )

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

}
