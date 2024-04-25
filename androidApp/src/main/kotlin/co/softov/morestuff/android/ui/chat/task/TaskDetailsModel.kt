package co.softov.morestuff.android.ui.chat.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.data.utils.toDayStartUtcTimeMillis
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.isOneTime
import co.softov.morestuff.android.domain.model.isReminder
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.task.GetTaskFlowUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.model.ScheduleUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@Composable
fun taskDetailsModel(
    taskId: Long,
    timeManager: TimeManager = koinInject(),
    timeFormatter: TimeFormatter = koinInject(),
    getTaskFlow: GetTaskFlowUseCase = koinInject(),
    store: AppStore = koinInject(),
    events: Flow<TaskDetailsEvent>,
    initialState: TaskDetailsState = TaskDetailsState()
): TaskDetailsState {
    var task by remember { mutableStateOf(initialState.task) }
    var scheduleModel by remember { mutableStateOf(initialState.scheduleModel) }
    var reminderModel by remember { mutableStateOf(initialState.reminderModel) }
    var taskTitle by remember { mutableStateOf(initialState.taskTitle) }

    LaunchedEffect(taskId) {
        getTaskFlow(taskId).collect { updatedTask ->
            task = updatedTask
            scheduleModel = createModelForSchedule(
                updatedTask.schedule.firstOrNull { it.isOneTime() },
                timeManager,
                timeFormatter
            )
            reminderModel = createModelForSchedule(
                updatedTask.schedule.firstOrNull { it.isReminder() },
                timeManager,
                timeFormatter
            )
            taskTitle = updatedTask.title
        }
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is TaskDetailsEvent.UpdateTaskTitle -> {
                    taskTitle = event.title
                    if (event.title.isNotEmpty()) {
                        store.dispatch(TaskAction.UpdateTaskTitleAction(taskId, event.title))
                    }
                }

                is TaskDetailsEvent.ToggleTaskComplete -> {
                    val isComplete = !task.isComplete
                    store.dispatch(TaskAction.CompleteTasksAction(listOf(taskId), isComplete))
                }

                is TaskDetailsEvent.CreateOneTimeSchedule -> {
                    val newSchedule = createScheduleModel(
                        timeManager.getDefaultPlanTime(),
                        timeManager,
                        timeFormatter
                    )
                    scheduleModel = newSchedule
                    store.dispatch(
                        ScheduleAction.RescheduleTaskAction(
                            taskId,
                            ScheduleType.OneTime,
                            newSchedule.scheduleLocalDateTime
                        )
                    )
                }

                is TaskDetailsEvent.UpdatePlanTime -> {
                    scheduleModel?.let {
                        val updatedPlanTime = timeManager.localDateTime(
                            it.scheduleLocalDateTime,
                            event.hour,
                            event.minute
                        )
                        scheduleModel =
                            createScheduleModel(updatedPlanTime, timeManager, timeFormatter)
                        store.dispatch(
                            ScheduleAction.RescheduleTaskAction(
                                taskId,
                                ScheduleType.OneTime,
                                updatedPlanTime
                            )
                        )
                    }
                }

                is TaskDetailsEvent.UpdatePlanDate -> {
                    scheduleModel?.let {
                        val updatedPlanTime = timeManager.utcMillisToLocalDateTime(
                            event.dateMillis,
                            it.scheduleLocalDateTime.hour,
                            it.scheduleLocalDateTime.minute
                        )
                        scheduleModel =
                            createScheduleModel(updatedPlanTime, timeManager, timeFormatter)
                        store.dispatch(
                            ScheduleAction.RescheduleTaskAction(
                                taskId,
                                ScheduleType.OneTime,
                                updatedPlanTime
                            )
                        )
                    }
                }

                is TaskDetailsEvent.CancelActiveSchedule -> {
                    store.dispatch(ScheduleAction.CancelActiveScheduleAction(taskId))
                }

                is TaskDetailsEvent.ScheduleResponse -> {
                    store.dispatch(
                        ReminderAction.UserResponseAction(
                            event.scheduleId,
                            event.replyType
                        )
                    )
                }
            }
        }
    }

    return TaskDetailsState(
        task = task,
        scheduleModel = scheduleModel,
        reminderModel = reminderModel,
        taskTitle = taskTitle
    )
}


private fun createModelForSchedule(
    scheduleDomain: ScheduleDomain?, timeManager: TimeManager,
    timeFormatter: TimeFormatter,
) =
    scheduleDomain?.scheduleLocalTime?.let {
        val localTime = scheduleDomain.scheduleLocalTime.toLocalDateTime()
        createScheduleModel(localTime, timeManager, timeFormatter)
    }

private fun createScheduleModel(
    time: LocalDateTime,
    timeManager: TimeManager,
    timeFormatter: TimeFormatter
): ScheduleUiModel {
    return ScheduleUiModel(
        scheduleLocalDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(time.toString()) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(time.toString()) ?: "Error",
        scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
        dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
        currentUtcTimeMillis = timeManager.nowUtcMillis
    )
}
