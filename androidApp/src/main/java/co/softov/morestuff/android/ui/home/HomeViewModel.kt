package co.softov.morestuff.android.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.*
import co.softov.morestuff.android.ui.model.NotificationState.Complete
import co.softov.morestuff.android.ui.model.NotificationState.None
import co.softov.morestuff.android.ui.model.NotificationState.TaskMovedToNewScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    state: SavedStateHandle,
    getScopesFlowUseCase: GetScopesFlowUseCase,
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    val selectedTasks = MutableStateFlow<List<Long>>(listOf())
    val initialState = state["Scopes"] ?: listOf(scopeAll)

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

            UndoComplete -> {
                dispatchAppStoreAction(
                    TaskAction.CompleteTasksAction(recentlyCompletedTasks, false)
                )

                copy(
                    recentlyCompletedTasks = listOf(),
                    notification = None
                )
            }

            is CompleteTask -> with(state) {
                dispatchAppStoreAction(
                    TaskAction.CompleteTasksAction(
                        listOf(event.taskId),
                        true
                    )
                )
                copy(
                    recentlyCompletedTasks = listOf(event.taskId),
                    notification = Complete
                )
            }

            CompleteSelectedTasks -> {
                val completedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(TaskAction.CompleteTasksAction(completedTasks, true))
                copy(
                    recentlyCompletedTasks = completedTasks,
                    notification = Complete
                )
            }

            DeleteSelectedTasks -> {
                val deletedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(TaskAction.DeleteTasksAction(deletedTasks))
                this
            }

            is SetNotification -> copy(notification = event.notification)

            is ToggleTaskSelection -> {
                selectedTasks.update {
                    if (event.taskId in it) it - event.taskId else it + event.taskId
                }
                this
            }

            is AddSelectedTasksToScope -> {
                val selectedTasks = selectedTasks.getAndUpdate { listOf() }
                dispatchAppStoreAction(
                    TaskAction.UpdateTasksToScopeAction(
                        selectedTasks,
                        event.scopeId
                    )
                )
                copy(
                    lastScopeId = event.scopeId,
                    recentlyMovedTasks = selectedTasks,
                    notification = TaskMovedToNewScope
                )
            }

            is UndoMoveTasks -> {
                dispatchAppStoreAction(
                    TaskAction.RemoveTasksFromScopeAction(recentlyMovedTasks, event.scopeId)
                )
                copy(
                    recentlyMovedTasks = listOf(),
                    notification = None
                )
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

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            delay(120)
            sendEvent(CompleteTask(taskId))
        }
    }

    fun toggleQuickReminder(taskId: Long) {
        dispatchAppStoreAction(ScheduleAction.ToggleReminderScheduleAction(taskId))
    }

    fun undoLastCompleted() {
        sendEvent(UndoComplete)
    }

    fun undoMovedTask() {
        val scopeId = state.lastScopeId
        scopeId?.let { UndoMoveTasks(it) }?.let { sendEvent(it) }
    }

    fun resetNotification() {
        sendEvent(SetNotification(None))
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

    fun clearSelectedTasks() {
        sendEvent(ClearTaskSelection)
    }

    fun addSelectedTasksToScope(scopeId: Long) {
        sendEvent(AddSelectedTasksToScope(scopeId))
    }

    fun removeSelectedTaskFromScope() {
        sendEvent(DeleteSelectedTasksFromScope)
    }

    fun selectScope(scopeId: Long) {
        Timber.d("Updating scopeId")
        sendEvent(ScopeSelected(scopeId))
    }

}


