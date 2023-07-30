package co.softov.morestuff.android.ui.chat.task


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.ClipboardHelper
import co.softov.morestuff.android.domain.service.ImageHandler
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.home.PlanModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
    getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase,
    getTaskFlow: GetTaskFlowUseCase,
    devTools: DevTools,
) : NoStateViewModel() {

    init {
        Timber.d("TaskChatViewModel: $taskId")
    }

    val messages: StateFlow<List<Message>> = flow {
        while (true) {
            val showDebug = devTools.getDebugMessageSwitchState()
            val messages = if (showDebug) {
                getTaskMessagesFlowUseCase(taskId = taskId).first()
            } else {
                getTaskChatMessagesUseCase(taskId = taskId).first()
            }
            emit(messages)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )

    val task: StateFlow<TaskDomain> =
        getTaskFlow(taskId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = TaskDomain()
            )

    var planModel by mutableStateOf<PlanModel?>(null)
        private set

    var taskTitle by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            taskTitle = getTaskFlow(taskId = taskId).first().title
        }

        getActiveScheduleFlow(taskId)
            .map { it.orNull() }
            .onEach { createPlanModelForSchedule(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )
    }

    private fun createPlanModelForSchedule(scheduleDomain: ScheduleDomain?) {
        planModel = scheduleDomain?.scheduleLocalTime?.let {
            val localTime = scheduleDomain.scheduleLocalTime.toLocalDateTime()
            createPlanTime(localTime)
        }
    }

    fun scheduleResponse(scheduleId: Long, replyType: ReplyType) {
        store.dispatch(UserResponseAction(scheduleId, replyType))
    }

    fun updateTaskTitle(title: String) {
        taskTitle = title
        if (title.isNotEmpty()) {
            dispatchAppStoreAction(TaskAction.UpdateTaskTitleAction(taskId, title))
        }
    }

    fun toggleTaskComplete() {
        store.dispatch(TaskAction.CompleteTaskAction(taskId = taskId, !task.value.isComplete))
    }

    fun sendTaskChatMessage(content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

    fun sendImageMessageForTask(uris: String, message: String) {
        store.dispatch(MessageAction.CreateImageMessageAction(taskId, uris, message))
    }


    private fun createPlanTime(
        time: LocalDateTime = timeManager.getDefaultPlanTime()
    ) = PlanModel(
        localDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "Error"
    )

    fun createOneTimeSchedule() {
        createPlanTime().run {
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, localDateTime)
            )
        }
    }

    fun updatePlanTime(hour: Int, minute: Int) {
        planModel?.let {
            val updatedPlanTime = timeManager.localDateTime(it.localDateTime, hour, minute)
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, updatedPlanTime)
            )
        }
    }

    fun updatePlanDate(dateMillis: Long) {
        planModel?.let {
            val updatedPlanTime =
                timeManager.epochMillisToLocalDateTime(dateMillis, it.hour, it.minute)
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, updatedPlanTime)
            )
        }
    }

    fun cancelActiveSchedule() {
        store.dispatch(ScheduleAction.CancelActiveScheduleAction(taskId))
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

}