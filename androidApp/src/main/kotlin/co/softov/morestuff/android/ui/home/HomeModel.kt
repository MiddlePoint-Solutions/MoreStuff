package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.CreateScopeUseCase
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import co.softov.morestuff.android.ui.home.HomeEvent.*
import co.softov.morestuff.android.ui.model.NotificationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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

    LaunchedEffect(Unit) {
        getScopesFlowUseCase().collect {
            scopes = it
        }
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                ClearTaskSelection -> {
                    selectedTasks = listOf()
                }

                CompleteSelectedTasks -> {
                    val completed = selectedTasks.toList()
                    selectedTasks = listOf()
                    store.dispatch(TaskAction.CompleteTasksAction(completed, true))

                    val notification = NotificationState.Complete {
                        store.dispatch(TaskAction.CompleteTasksAction(completed, false))
                    }
                    notifications.tryEmit(notification)
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
                    val notification = NotificationState.TaskMovedToScope({})
                    notifications.tryEmit(notification)
                }

                is CreateScopeForSelectedTasks -> {
                    createScopeUseCase(event.title).onRight { scope ->
                        store.dispatch(
                            TaskAction.UpdateTasksToScopeAction(selectedTasks, scope.id)
                        )
                        val notification = NotificationState.TaskMovedToScope({})
                        notifications.tryEmit(notification)
                    }
                }

                is ScopeSelected -> currentScopeId = event.scopeId
                is ToggleTaskSelection -> {
                    selectedTasks = if (event.taskId in selectedTasks) {
                        selectedTasks - event.taskId
                    } else {
                        selectedTasks + event.taskId
                    }
                }
            }
        }
    }

    return HomeState(
        currentScopeId = currentScopeId,
        selectedTasks = selectedTasks,
        scopes = scopes
    )
}
