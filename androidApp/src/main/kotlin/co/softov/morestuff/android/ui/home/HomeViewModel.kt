package co.softov.morestuff.android.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.*
import co.softov.morestuff.android.ui.model.NotificationState
import co.softov.morestuff.android.ui.model.NotificationState.TaskMovedToNewScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber

class HomeViewModel(
    state: SavedStateHandle,
    getScopesFlowUseCase: GetScopesFlowUseCase,
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    private val initialState = state["Scopes"] ?: listOf(defaultScope)
    val selectedTasks = MutableStateFlow<List<Long>>(listOf())

    internal val notifications =  MutableSharedFlow<NotificationState>()

    val scopes = getScopesFlowUseCase()
        .onEach { scopes ->
            Timber.d("Scope order: ${scopes.map { it.name }}")
            state["Scopes"] = scopes
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = initialState
        )

    init {
        loadData()
    }

    override fun onAppStateChange(state: AppState) {
        sendEvent(SetConfettiEnabled(state.settings.enableConfetti))
    }

    override fun onReduceState(event: HomeUiEvent): HomeUiModel = state.run {
        return when (event) {
            ClearTaskSelection -> {
                selectedTasks.update { listOf() }
                this
            }

            is UndoComplete -> {
                dispatchAppStoreAction(
                    TaskAction.CompleteTasksAction(event.tasks, false)
                )
                this
            }

            CompleteSelectedTasks -> {
                val completedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(TaskAction.CompleteTasksAction(completedTasks, true))
//                notification = Complete(
//                    action = { sendEvent(UndoComplete(completedTasks)) }
//                )
                this
            }

            DeleteSelectedTasks -> {
                val deletedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(TaskAction.DeleteTasksAction(deletedTasks))
                this
            }

            is ToggleTaskSelection -> {
                selectedTasks.update {
                    if (event.taskId in it) it - event.taskId else it + event.taskId
                }
                this
            }

            is MoveSelectedTasksToScope -> {
                val selectedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(
                    TaskAction.UpdateTasksToScopeAction(
                        selectedTasks,
                        event.scopeId
                    )
                )

                val fromScope = scopes.value.first { it.id == selectedScopeId }
                val notificaiton = TaskMovedToNewScope(
                    from = fromScope,
                    action = { sendEvent(UndoMoveTasks(fromScope.id, selectedTasks)) }
                )

                launch { notifications.emit(notificaiton) }
                this
            }

            is UndoMoveTasks -> {
                dispatchAppStoreAction(
                    TaskAction.UpdateTasksToScopeAction(event.tasks, event.fromScopeId)
                )
//                notification = None
                this
            }

            is DeleteSelectedTasksFromScope -> {
                val selectedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(
                    TaskAction.RemoveTasksFromScopeAction(selectedTasks, selectedScopeId)
                )
                this
            }

            is ScopeSelected -> copy(selectedScopeId = event.scopeId)

            is SetConfettiEnabled -> state.copy(confettiEnabled = event.enabled)

        }
    }

    fun toggleQuickReminder(taskId: Long) {
        dispatchAppStoreAction(ScheduleAction.ToggleReminderScheduleAction(taskId))
    }

    fun completeSelectedTasks() {
        sendEvent(CompleteSelectedTasks)
    }

    fun deleteSelectedTasks() {
        sendEvent(DeleteSelectedTasks)
    }

    fun toggleTaskSelection(taskId: Long) {
        sendEvent(ToggleTaskSelection(taskId))
    }

    fun resetNotification() {
//        notification = None
    }

    fun clearSelectedTasks() {
        sendEvent(ClearTaskSelection)
    }

    fun addSelectedTasksToScope(scopeId: Long) {
        sendEvent(MoveSelectedTasksToScope(scopeId))
    }

    fun removeSelectedTaskFromScope() {
        sendEvent(DeleteSelectedTasksFromScope)
    }

    fun selectScope(scopeId: Long) {
        sendEvent(ScopeSelected(scopeId))
    }

}


