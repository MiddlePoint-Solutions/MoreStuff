package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.data.utils.scheduleLocalDateTime
import io.middlepoint.morestuff.shared.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.core.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.model.core.ScopeDomain
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.ui.model.NotificationState
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ClearPlanPriority
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CompleteSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CompleteTask
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateScope
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateScopeForSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateTask
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.CreateTaskWithSchedule
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.DeleteSelectedTasks
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.HideTaskInput
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.MoveSelectedTasksToScope
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ResetHomeState
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ScopeSelected
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.SetPlanPriority
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ShowTaskInput
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ToggleScopeReordering
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.ToggleTaskSelection
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.UpdatePlanDate
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.UpdatePlanTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay


@Composable
fun homeModel(
  initialState: HomeState,
  events: Flow<HomeEvent>,
  notifications: MutableSharedFlow<NotificationState>,
  store: AppStore = koinInject(),
  getScopesFlowUseCase: GetScopesFlowUseCase = koinInject(),
  createScopeUseCase: CreateScopeUseCase = koinInject(),
  timeManager: TimeManager = koinInject(),
  timeFormatter: TimeFormatter = koinInject(),
): HomeState {

  var scopes: List<ScopeDomain> by remember { mutableStateOf(initialState.scopes) }
  var currentScopeId: Long by remember { mutableLongStateOf(initialState.currentScopeId) }
  var selectedTasks: List<Long> by remember { mutableStateOf(initialState.selectedTasks) }
  var taskInputActive: Boolean by remember { mutableStateOf(initialState.taskInputActive) }
  var reorderingScopes: Map<Long, Boolean> by remember { mutableStateOf(initialState.reorderingScopes) }
  var scheduleModel by remember { mutableStateOf(initialState.scheduleModel) }
  var lastCreatedTaskId by remember { mutableLongStateOf(initialState.lastCreatedTaskId ?: -1) }
  var planTime by remember { mutableStateOf(initialState.planTime) }
  val taskSchedules: Map<Long, ScheduleDomain> by remember { mutableStateOf(emptyMap()) }
  var tasks: Map<Long, TaskUiModel> by remember { mutableStateOf(initialState.tasks) }
  var username by remember { mutableStateOf(initialState.username) }

  fun dispatch(action: Action) = store.dispatch(action)
  val pendingCompletionTasks = remember { mutableStateMapOf<Long, Job>() }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    getScopesFlowUseCase().collect { scopes = it }
  }

  LaunchedEffect(Unit) {
    store.state
      .distinctUntilChangedBy { it.userState.user }
      .collectLatest { state ->
        Logger.d("user = ${state.userState.user}")
        val newUsername = state.userState.user?.fullName
          .takeUnless { it.isNullOrBlank() }
          ?: state.userState.user?.email.orEmpty()
        Logger.d("username = $newUsername")
        username = newUsername
      }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        is CreateTask -> {
          if (event.title.isNotBlank()) {
            dispatch(TaskAction.CreateUserTaskAction(event.title, Priority.Now(), currentScopeId))
          }
          taskInputActive = false
        }

        is SetPlanPriority -> {
          planTime = createPlanTime(timeManager, timeFormatter)
        }

        is ClearPlanPriority -> {
          planTime = null
        }

        is CreateTaskWithSchedule -> {
          if (event.title.isNotBlank()) {
            dispatch(
              TaskAction.CreateUserTaskAction(
                event.title,
                Priority.Now(),
                currentScopeId,
                onTaskCreated = { createdTask ->
                  lastCreatedTaskId = createdTask.id
                  planTime?.let { schedule ->
                    scheduleModel = schedule
                    store.dispatch(
                      ScheduleAction.RescheduleTaskAction(
                        createdTask.id,
                        ScheduleType.OneTime,
                        schedule.scheduleLocalDateTime
                      )
                    )
                  }
                }
              )
            )
          }
          taskInputActive = false
        }

        ShowTaskInput -> {
          taskInputActive = true
          planTime = null
        }

        ResetHomeState -> {
          selectedTasks = listOf()
          reorderingScopes = emptyMap()
        }

        HideTaskInput -> {
          taskInputActive = false
        }

        is CompleteSelectedTasks -> {
          val completed = selectedTasks.toList()
          val schedulesToRestore = completed.mapNotNull { taskId ->
            taskSchedules[taskId]?.let { schedule -> taskId to schedule }
          }.toMap()

          selectedTasks = listOf()
          store.dispatch(TaskAction.CompleteTasksAction(completed, true))

          if (completed.isNotEmpty()) {
            val notification = NotificationState.Complete {
              store.dispatch(TaskAction.CompleteTasksAction(completed, false))

              schedulesToRestore.forEach { (taskId, schedule) ->
                schedule.scheduleLocalDateTime?.let { dateTime ->
                  store.dispatch(
                    ScheduleAction.RescheduleTaskAction(
                      taskId,
                      schedule.scheduleType,
                      dateTime
                    )
                  )
                }
              }
            }
            launch { notifications.emit(notification) }
          }
        }

        DeleteSelectedTasks -> {
          store.dispatch(TaskAction.DeleteTasksAction(selectedTasks))

        }

        is UpdatePlanDate -> {
          planTime = planTime?.let {
            val updatedTime = timeManager.utcMillisToLocalDateTime(
              event.dateMillis,
              it.hour,
              it.minute
            )
            createPlanTime(
              timeManager,
              timeFormatter,
              updatedTime
            )
          }

        }

        is UpdatePlanTime -> {
          planTime = planTime?.let {
            val updatedTime = timeManager.localDateTime(
              it.scheduleLocalDateTime,
              event.hour,
              event.minute
            )
            createPlanTime(
              timeManager,
              timeFormatter,
              updatedTime
            )
          }
        }

        DeleteSelectedTasks -> {
          store.dispatch(TaskAction.DeleteTasksAction(selectedTasks))
          selectedTasks = listOf()
        }

        is MoveSelectedTasksToScope -> {
          val moved = selectedTasks.toList()
          selectedTasks = listOf()
          store.dispatch(
            TaskAction.UpdateTasksToScopeAction(moved, event.scopeId)
          )
          val scopeTitle = scopes.firstOrNull { it.id == event.scopeId }?.name ?: ""
          val notification = NotificationState.TaskMovedToScope(scopeTitle)
          launch { notifications.emit(notification) }
        }

        is CreateScopeForSelectedTasks -> {
          val selected = selectedTasks.toList()
          selectedTasks = listOf()
          createScopeUseCase(event.title).onRight { scope ->
            store.dispatch(
              TaskAction.UpdateTasksToScopeAction(selected, scope.id)
            )
            val notification = NotificationState.TaskMovedToScope(scope.name)
            launch { notifications.emit(notification) }
          }
        }

        is CreateScope -> {
          createScopeUseCase(event.title)
        }

        is ScopeSelected -> {
          currentScopeId = event.scopeId
        }

        is ToggleTaskSelection -> {
          val taskId = event.taskId
          event.task?.let { task ->
            tasks = tasks + (taskId to task)
          }
          selectedTasks = if (event.taskId in selectedTasks) {
            selectedTasks - event.taskId
          } else {
            selectedTasks + event.taskId
          }
        }

        is ToggleScopeReordering -> {
          reorderingScopes = reorderingScopes.toMutableMap().also {
            it[event.scopeId] = event.isReordering
          }
        }

        is CompleteTask -> {
          handleDelayTaskCompletion(
            taskId = event.taskId,
            pendingCompletionTasks = pendingCompletionTasks,
            coroutineScope = coroutineScope,
            onComplete = suspend {
              val schedule = taskSchedules[event.taskId]
              store.dispatch(TaskAction.CompleteTasksAction(listOf(event.taskId), true))
              selectedTasks = selectedTasks - event.taskId
              val notification = NotificationState.Complete {
                store.dispatch(TaskAction.CompleteTasksAction(listOf(event.taskId), false))
                schedule?.scheduleLocalDateTime?.let { dateTime ->
                  store.dispatch(
                    ScheduleAction.RescheduleTaskAction(
                      event.taskId,
                      schedule.scheduleType,
                      dateTime
                    )
                  )
                }
                selectedTasks = selectedTasks + event.taskId
              }
              notifications.emit(notification)
            }
          )
        }
      }
    }
  }

  return HomeState(
    username = username,
    currentScopeId = currentScopeId,
    selectedTasks = selectedTasks,
    scopes = scopes,
    taskInputActive = taskInputActive,
    reorderingScopes = reorderingScopes,
    planTime = planTime,
    tasks = tasks
  )
}

private fun createPlanTime(
  timeManager: TimeManager,
  timeFormatter: TimeFormatter,
  time: LocalDateTime = timeManager.getDefaultPlanTime(),
) = ScheduleUiModel(
  scheduleLocalDateTime = time,
  displayDate = timeFormatter.formatDisplayDate(time.date),
  displayTime = timeFormatter.formatDisplayTime(time.time) ?: "--:--",
  scheduleUtcTimeMillis = timeManager.localDateTimeToUtc(time).toEpochMilliseconds(),
  dayStartUtcTimeMillis = timeManager.nowLocalDateTime.toDayStartUtcTimeMillis(),
  currentUtcTimeMillis = timeManager.nowUtcMillis
)

private fun handleDelayTaskCompletion(
  taskId: Long,
  pendingCompletionTasks: MutableMap<Long, Job>,
  coroutineScope: CoroutineScope,
  onComplete: suspend () -> Unit
) {
  if (pendingCompletionTasks.contains(taskId)) {
    pendingCompletionTasks[taskId]?.cancel()
    pendingCompletionTasks.remove(taskId)
  } else {
    val job = coroutineScope.launch {
      delay(1000)
      onComplete()
      pendingCompletionTasks.remove(taskId)
    }
    pendingCompletionTasks[taskId] = job
  }
}