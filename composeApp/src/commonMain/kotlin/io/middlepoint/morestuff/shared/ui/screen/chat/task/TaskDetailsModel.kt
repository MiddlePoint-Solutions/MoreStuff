package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.model.isOneTime
import io.middlepoint.morestuff.shared.domain.model.isReminder
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.ReminderAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.TaskAction
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.compose.koinInject

@Composable
fun taskDetailsModel(
    taskId: Long,
    initialState: TaskDetailsState,
    events: Flow<TaskDetailsEvent>,
    timeManager: TimeManager = koinInject(),
    timeFormatter: TimeFormatter = koinInject(),
    getTaskFlow: GetTaskFlowUseCase = koinInject(),
    store: AppStore = koinInject(),
): TaskDetailsState {

    var task by remember { mutableStateOf(initialState.task) }
    var scheduleModel by remember { mutableStateOf(initialState.scheduleModel) }
    var reminderModel by remember { mutableStateOf(initialState.reminderModel) }
    var taskTitle by remember { mutableStateOf(initialState.taskTitle) }

    LaunchedEffect(Unit) {
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
        val localTime = LocalDateTime.parse(it)
        createScheduleModel(localTime, timeManager, timeFormatter)
    }

private fun createScheduleModel(
    time: LocalDateTime,
    timeManager: TimeManager,
    timeFormatter: TimeFormatter
): ScheduleUiModel {
    val timeString = time.toInstant(TimeZone.UTC).toString()
    return ScheduleUiModel(
        scheduleLocalDateTime = time,
        displayDate = timeFormatter.formatTimeDayAndMonth(timeString) ?: "Error",
        displayTime = timeFormatter.formatTimeOnly(timeString) ?: "Error",
        scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
        dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
        currentUtcTimeMillis = timeManager.nowUtcMillis
    )
}
