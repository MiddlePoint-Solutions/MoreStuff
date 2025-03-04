package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.middlepoint.morestuff.shared.domain.model.Priority
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.TaskAction
import io.middlepoint.morestuff.shared.domain.redux.store.Action
import io.middlepoint.morestuff.shared.domain.usecase.scope.CreateScopeUseCase
import io.middlepoint.morestuff.shared.domain.usecase.scope.GetScopesFlowUseCase
import io.middlepoint.morestuff.shared.ui.screen.home.HomeEvent.*
import io.middlepoint.morestuff.shared.ui.model.NotificationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun homeModel(
  initialState: HomeState,
  events: Flow<HomeEvent>,
  notifications: MutableSharedFlow<NotificationState>,
  store: AppStore = koinInject(),
  getScopesFlowUseCase: GetScopesFlowUseCase = koinInject(),
  createScopeUseCase: CreateScopeUseCase = koinInject()
): HomeState {

  var scopes: List<ScopeDomain> by remember { mutableStateOf(initialState.scopes) }
  var currentScopeId: Long by remember { mutableLongStateOf(initialState.currentScopeId) }
  var selectedTasks: List<Long> by remember { mutableStateOf(initialState.selectedTasks) }
  var taskInputActive: Boolean by remember { mutableStateOf(initialState.taskInputActive) }
  var reorderingScopes: Map<Long, Boolean> by remember { mutableStateOf(initialState.reorderingScopes) }


  fun dispatch(action: Action) = store.dispatch(action)

  LaunchedEffect(Unit) {
    getScopesFlowUseCase().collect {
      scopes = it
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

        ShowTaskInput -> {
          taskInputActive = true
        }

        ResetHomeState -> {
          selectedTasks = listOf()
          taskInputActive = false
          reorderingScopes = emptyMap()
        }

        is CompleteSelectedTasks -> {
          val completed = selectedTasks.toList()
          selectedTasks = listOf()
          store.dispatch(TaskAction.CompleteTasksAction(completed, true))
          if (completed.isNotEmpty()) {
            val notification = NotificationState.Complete {
              store.dispatch(TaskAction.CompleteTasksAction(completed, false))
            }
            launch { notifications.emit(notification) }
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

        is ScopeSelected -> currentScopeId = event.scopeId
        is ToggleTaskSelection -> {
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

      }
    }
  }

  return HomeState(
    currentScopeId = currentScopeId,
    selectedTasks = selectedTasks,
    scopes = scopes,
    taskInputActive = taskInputActive,
    reorderingScopes = reorderingScopes
  )
}
