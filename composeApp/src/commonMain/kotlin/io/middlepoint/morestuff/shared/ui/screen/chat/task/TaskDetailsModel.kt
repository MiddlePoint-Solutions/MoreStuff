package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.model.core.isOneTime
import io.middlepoint.morestuff.shared.domain.model.core.isReminder
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.task.GetTaskFlowUseCase
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@Composable
fun taskDetailsModel(
  taskId: Uuid,
  initialState: TaskDetailsState,
  events: Flow<TaskDetailsEvent>,
  taskUiMapper: TaskUiMapper = koinInject(),
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
      task = taskUiMapper.map(updatedTask)
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
          val isComplete = task?.isComplete == false
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
            NotificationAction.UserResponseAction(
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
  schedule: Schedule?,
  timeManager: TimeManager,
  timeFormatter: TimeFormatter,
) =
  schedule?.scheduledAt?.let {
    Logger.d { "SCHEDULE: $it" }
    val localTime = Instant.parse(it).toLocalDateTime(TimeZone.of(schedule.timezone))
    createScheduleModel(localTime, timeManager, timeFormatter)
  }

private fun createScheduleModel(
  time: LocalDateTime,
  timeManager: TimeManager,
  timeFormatter: TimeFormatter
): ScheduleUiModel {
  return ScheduleUiModel(
    scheduleLocalDateTime = time,
    displayDate = timeFormatter.formatDisplayDate(time.date),
    displayTime = timeFormatter.formatDisplayTime(time.time),
    scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
    dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
    currentUtcTimeMillis = timeManager.nowUtcMillis
  )
}
