package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import co.softov.morestuff.android.data.utils.inEpochMilliseconds
import co.softov.morestuff.android.domain.DevTools
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.MessageAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.repository.MessageRepository



import co.softov.morestuff.android.domain.service.TimeManager

import co.softov.morestuff.android.domain.usecase.message.GetTaskChatMessagesUseCase
import co.softov.morestuff.android.domain.usecase.message.GetTaskMessagesFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetActiveScheduleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.UpdateTaskTitleUseCase
import co.softov.morestuff.android.ui.main.PlanModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

class TaskChatViewModel(
    getTaskChatMessagesUseCase: GetTaskChatMessagesUseCase,
    getActiveScheduleFlow: GetActiveScheduleFlowUseCase,
    getTaskMessagesFlowUseCase: GetTaskMessagesFlowUseCase,
    private val getTaskFlow: GetTaskFlowUseCase,
    private val updateTaskTitleUseCase: UpdateTaskTitleUseCase,
    private val taskId: Long,
    private val messageRepository: MessageRepository,


    private val timeManager: TimeManager,

    devTools: DevTools,
) : NoStateViewModel() {

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

    var planModel by mutableStateOf(createPlanModel())
        private set

    val schedule: StateFlow<ScheduleDomain?> =
        getActiveScheduleFlow(taskId)
            .map { it.orNull() }
            .onEach { it?.let { createPlanModelForScheduleVal(it) } }
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

    private fun createPlanModelForScheduleVal(scheduleDomain: ScheduleDomain) {
        if (scheduleDomain.scheduleLocalTime != null) {
            val localTime = scheduleDomain.scheduleLocalTime.toLocalDateTime()
            planModel = PlanModel(
                planTime = localTime,
                relativeDisplay = timeManager.getRelativeDate(scheduleDomain.scheduleLocalTime),
                hour = localTime.hour,
                minute = localTime.minute,
                epochMs = scheduleDomain.scheduleLocalTime.inEpochMilliseconds
            )
            Timber.d("planplan: $planModel")
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

    fun setTaskComplete(complete: Boolean) {
        store.dispatch(TaskAction.CompleteTaskAction(taskId = taskId, complete))
    }

    fun sendMessageForTask(content: String) {
        store.dispatch(MessageAction.CreateUserTaskMessageAction(taskId, content))
    }

    private fun createPlanModel() = timeManager.getDefaultPlanTime().run {
        PlanModel(
            planTime = this,
            hour = hour,
            minute = minute,
            epochMs = currentTimeZoneInstant.toEpochMilliseconds()
        )
    }

    fun createPlanAndReschedule() {
        val newPriority = Priority.Plan(planModel.planTime.toString())
        store.dispatch(ScheduleAction.RescheduleTaskAction(taskId, newPriority))
    }

    fun updatePlanTime(hour: Int, minute: Int) {
        val updatedPlanTime = timeManager.localDateTime(planModel.planTime, hour, minute)
        planModel = planModel.copy(
            planTime = updatedPlanTime,
            hour = hour,
            minute = minute,
            epochMs = updatedPlanTime.currentTimeZoneInstant.toEpochMilliseconds()
        )
    }

    fun updatePlanDate(dateMillis: Long) {
        Timber.d("updatePlanDate: $dateMillis")
        val updatedPlanTime =
            timeManager.epochMillisToLocalDateTime(dateMillis, planModel.hour, planModel.minute)
        planModel = planModel.copy(
            planTime = updatedPlanTime,
            relativeDisplay = timeManager.getRelativeDate(updatedPlanTime.toString()),
            epochMs = updatedPlanTime.currentTimeZoneInstant.toEpochMilliseconds()
        )
        Timber.d("updatePlanDate: $planModel")

    }

    fun cancelActiveSchedule() {
        store.dispatch(ScheduleAction.CancelActiveScheduleAction(taskId))
        planModel = createPlanModel()
    }

    fun onBackPressed() {
        router.exit()
    }
}