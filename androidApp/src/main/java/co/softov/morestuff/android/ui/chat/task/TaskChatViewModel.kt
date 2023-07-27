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
import co.softov.morestuff.android.domain.service.ClipboardHandler
import co.softov.morestuff.android.domain.service.ImageHandler
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.message.DeleteMessageUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
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
    getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase,
    private val getTaskFlow: GetTaskFlowUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val clipboardHandler: ClipboardHandler,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val shareImage: ImageHandler,
    private val taskId: Long,
    private val timeManager: TimeManager,
    private val timeFormatter: TimeFormatter,
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

    private val schedule: StateFlow<ScheduleDomain?> =
        getActiveScheduleFlow(taskId)
            .map { it.orNull() }
            .onEach { createPlanModelForSchedule(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    var taskTitle by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            taskTitle = getTaskFlow(taskId = taskId).first().title
        }
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
            viewModelScope.launch {
                updateTaskTitleUseCase(taskId, title)
            }
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
                ScheduleAction.RescheduleTaskAction(
                    taskId,
                    ScheduleType.OneTime,
                    localDateTime.toString()
                )
            )
        }
    }

    fun updatePlanTime(hour: Int, minute: Int) {
//        val updatedPlanTime = timeManager.localDateTime(planModel.localDateTime, hour, minute)
    }

    fun updatePlanDate(dateMillis: Long) {
        Timber.d("updatePlanDate: $dateMillis")
//        TODO("updatePlanDate")
        Timber.d("updatePlanDate: $planModel")
    }

    fun cancelActiveSchedule() {
        store.dispatch(ScheduleAction.CancelActiveScheduleAction(taskId))
    }

    fun copyToClipboard(text: String) {
        clipboardHandler.copyToClipboard(text)
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            deleteMessageUseCase(messageId)
        }
    }

    fun shareImage(imagePath: String) {
        viewModelScope.launch {
            shareImage.shareImage(imagePath)
        }
    }

}