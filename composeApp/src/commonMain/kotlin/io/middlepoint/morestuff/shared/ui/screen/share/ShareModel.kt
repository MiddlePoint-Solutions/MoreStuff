package io.middlepoint.morestuff.shared.ui.screen.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.middleware.TaskAction
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.SearchTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.TaskParams
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.ClearSearchQuery
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.CreateNewTask
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.CreateTaskWithSchedule
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.ResetShareState
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.ScopeSelected
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.SetPlanPriority
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.ShowTaskInput
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.UpdatePlanDate
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.UpdatePlanTime
import io.middlepoint.morestuff.shared.ui.screen.share.ShareEvent.UpdateSearchQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Composable
fun shareModel(
  initialState: ShareModel,
  events: Flow<ShareEvent>,
  store: AppStore = koinInject(),
  getScopesFlowUseCase: GetScopesFlowUseCase = koinInject(),
  searchTasksUseCase: SearchTasksUseCase = koinInject(),
  createTaskUseCase: CreateTaskUseCase = koinInject(),
  taskUiMapper: TaskUiMapper = koinInject(),
  timeManager: TimeManager = koinInject(),
  timeFormatter: TimeFormatter = koinInject(),
): ShareModel {

  var state by remember(initialState) { mutableStateOf(initialState) }
  val searchQueryFlow = remember { MutableStateFlow("") }

  LaunchedEffect(Unit) {
    getScopesFlowUseCase().collect {
      state = state.copy(scopes = it)
    }
  }

  LaunchedEffect(Unit) {
    searchQueryFlow
      .debounce(300)
      .distinctUntilChanged()
      .flatMapLatest { searchTasksUseCase(it, true) }
      .map(taskUiMapper::map)
      .collect {
        state = state.copy(searchResults = it)
      }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        ResetShareState -> {
          state = state.copy(taskInputActive = false)
        }

        is ScopeSelected -> {
          state = state.copy(currentScopeId = event.scopeId)
        }

        ShowTaskInput -> {
          state = state.copy(taskInputActive = true)
        }

        is CreateNewTask -> {
          val domainPriority = Priority.Now()
          val trimmedTitle = event.title.trim()
          val params = TaskParams(trimmedTitle, domainPriority, TaskType.User, state.currentScopeId)
          val task = createTaskUseCase(params)
          store.dispatch(TaskAction.TaskCreatedAction(task, domainPriority))
          state = state.copy(createdTaskId = task.id)
//          launch {
//            // Hack for not using the same taskId in share screen.
//            // This will be solved when adding new navigation with decompose router
//            delay(300)
//            state = state.copy(
//              lastCreatedTaskId = null
//            )
//          }
        }

        ClearSearchQuery -> searchQueryFlow.update { "" }
        is UpdateSearchQuery -> searchQueryFlow.update { event.query }

        is CreateTaskWithSchedule -> {
          val domainPriority = Priority.Now()
          val trimmedTitle = event.title.trim()
          val params = TaskParams(trimmedTitle, domainPriority, TaskType.User, state.currentScopeId)
          val task = createTaskUseCase(params)

          store.dispatch(TaskAction.TaskCreatedAction(task, domainPriority))

          state = state.copy(createdTaskId = task.id)

          state.planTime?.let { schedule ->
            state = state.copy(scheduleModel = schedule)
            store.dispatch(
              ScheduleAction.RescheduleTaskAction(
                task.id,
                ScheduleType.OneTime,
                schedule.scheduleLocalDateTime
              )
            )
          }
        }



        is SetPlanPriority -> {
          state = state.copy(
            planTime = createPlanTime(timeManager, timeFormatter)
          )
        }

        is UpdatePlanDate -> {
          state = state.copy(
            planTime = state.planTime?.let {
              val updatedTime = timeManager.utcMillisToLocalDateTime(
                event.dateMillis,
                it.hour,
                it.minute
              )
              createPlanTime(timeManager, timeFormatter, updatedTime)
            }
          )
        }

        is UpdatePlanTime -> {
          state = state.copy(
            planTime = state.planTime?.let {
              val updatedTime = timeManager.localDateTime(
                it.scheduleLocalDateTime,
                event.hour,
                event.minute
              )
              createPlanTime(timeManager, timeFormatter, updatedTime)
            }
          )
        }

      }
    }
  }

  return state
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