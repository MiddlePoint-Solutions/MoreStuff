package co.softov.morestuff.android.ui.chat.task


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.data.utils.toDayStartUtcTimeMillis
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.model.isOneTime
import co.softov.morestuff.android.domain.model.isReminder
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

class TaskDetailsViewModel(
    private val taskId: Long,
    private val timeManager: TimeManager,
    private val timeFormatter: TimeFormatter,
    getTaskFlow: GetTaskFlowUseCase,
) : NoStateViewModel() {

    init {
        Timber.d("TaskDetailsViewModel: $taskId")
        viewModelScope.launch {
            taskTitle = getTaskFlow(taskId = taskId).first().title
        }
    }

    val task: StateFlow<TaskDomain> = getTaskFlow(taskId)
        .onEach { task ->
            scheduleModel = createModelForSchedule(task.schedule.firstOrNull { it.isOneTime() })
            reminderModel = createModelForSchedule(task.schedule.firstOrNull { it.isReminder() })
            taskTitle = task.title
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TaskDomain()
        )

    var scheduleModel by mutableStateOf<ScheduleUiModel?>(null)
        private set

    var reminderModel by mutableStateOf<ScheduleUiModel?>(null)
        private set

    var taskTitle by mutableStateOf("")
        private set

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

    fun createOneTimeSchedule() {
        createScheduleModel().run {
            dispatchAppStoreAction(
                ScheduleAction.RescheduleTaskAction(taskId, ScheduleType.OneTime, scheduleLocalDateTime)
            )
        }
    }

    fun updatePlanTime(hour: Int, minute: Int) {
        scheduleModel?.let {
            val updatedPlanTime = timeManager.localDateTime(it.scheduleLocalDateTime, hour, minute)
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

    private fun createModelForSchedule(scheduleDomain: ScheduleDomain?) =
        scheduleDomain?.scheduleLocalTime?.let {
            val localTime = scheduleDomain.scheduleLocalTime.toLocalDateTime()
            createScheduleModel(localTime)
        }


    private fun createScheduleModel(
        time: LocalDateTime = timeManager.getDefaultPlanTime(),
    ) = ScheduleUiModel(
        scheduleLocalDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "Error",
        dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
        currentUtcTimeMillis = timeManager.nowUtcMillis
    )

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }

}
