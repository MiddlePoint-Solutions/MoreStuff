package io.middlepoint.morestuff.shared.ui.screen.share

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.data.utils.toDayStartUtcTimeMillis
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import io.middlepoint.morestuff.shared.domain.redux.action.TaskAction
import io.middlepoint.morestuff.shared.domain.repository.TimeFormatter
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.CreateTaskUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.SearchTasksUseCase
import io.middlepoint.morestuff.shared.domain.usecase.task.TaskParams
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel
import io.middlepoint.morestuff.shared.ui.model.map.TaskUiMapper
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.ClearSearchQuery
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.CreateNewTask
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.CreateTaskWithSchedule
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.ResetShareState
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.ScopeSelected
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.SetPlanPriority
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.ShowTaskInput
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.UpdatePlanDate
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.UpdatePlanTime
import io.middlepoint.morestuff.shared.ui.screen.share.ImportEvent.UpdateSearchQuery
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
fun importModel(
  initialState: ImportModel,
  events: Flow<ImportEvent>,
  store: AppStore = koinInject(),
  getScopesFlowUseCase: GetScopesFlowUseCase = koinInject(),
  searchTasksUseCase: SearchTasksUseCase = koinInject(),
  createTaskUseCase: CreateTaskUseCase = koinInject(),
  taskUiMapper: TaskUiMapper = koinInject(),
  timeManager: TimeManager = koinInject(),
  timeFormatter: TimeFormatter = koinInject(),
): ImportModel {

  var state by remember(initialState) { mutableStateOf(initialState) }
  val searchQueryFlow = remember { MutableStateFlow("") }

  LaunchedEffect(Unit) {
    getScopesFlowUseCase().collect {
      state = state.copy(
        currentScopeId = it.first().id,
        scopes = it,
      )
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
          state = state.copy(taskInputActive = true, planTime = null)
        }

        is CreateNewTask -> {
          state.currentScopeId?.let { scopeId ->
            val domainPriority = Priority.Now()
            val trimmedTitle = event.title.trim()
            val params = TaskParams(trimmedTitle, domainPriority, scopeId)
            val task = createTaskUseCase(params)
            store.dispatch(TaskAction.TaskCreatedAction(task, domainPriority))
            state = state.copy(createdTaskId = task.id)
          }
        }

        ClearSearchQuery -> searchQueryFlow.update { "" }
        is UpdateSearchQuery -> searchQueryFlow.update { event.query }

        is CreateTaskWithSchedule -> {
          state.currentScopeId?.let { scopeId ->
            val domainPriority = Priority.Now()
            val trimmedTitle = event.title.trim()
            val params = TaskParams(trimmedTitle, domainPriority, scopeId)
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
        }


        is SetPlanPriority -> {
          state = state.copy(
            planTime = createPlanTime(timeManager, timeFormatter)
          )
        }

        is ImportEvent.ClearPlanPriority -> {
          state = state.copy(
            planTime = null
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

// TODO: this is duplicated
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