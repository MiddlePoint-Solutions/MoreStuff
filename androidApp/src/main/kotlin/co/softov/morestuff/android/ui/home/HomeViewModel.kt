package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import app.cash.molecule.moleculeFlow
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.MoleculeViewModel
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.ui.home.HomeUiEvent.*
import co.softov.morestuff.android.ui.model.NotificationState
import co.softov.morestuff.android.ui.model.NotificationState.TaskMovedToNewScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber

class HomeViewModel2(
    private val savedState: SavedStateHandle
) : MoleculeViewModel<HomeUiEvent, HomeScopeState>() {

    override val initialState: HomeScopeState = savedState["Scopes"] ?: HomeScopeState()

    @Composable
    override fun models(events: Flow<HomeUiEvent>): HomeScopeState {
        return homeScopeModel(initialState, events)
    }

    override fun onSaveState(model: HomeScopeState) {
        savedState["Scopes"] = model
    }
}

class HomeViewModel(
    savedState: SavedStateHandle
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    val selectedTasks = MutableStateFlow<List<Long>>(listOf())

    internal val notifications = MutableSharedFlow<NotificationState>(replay = 1)

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
                notifications.tryEmit(
                    NotificationState.Complete(action = { sendEvent(UndoComplete(completedTasks)) }
                    ))

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

                // TODO: this does not handle cases where tasks are moved from different scopes.
//                val fromScope =
//                    scopeState.value.scopes.first { scope -> scope.id == selectedScopeId }
//                val notification = TaskMovedToNewScope(
//                    from = fromScope,
//                    action = {
//                        // sendEvent(UndoMoveTasks(fromScope.id, selectedTasks))
//                    }
//                )
//
//                launch { notifications.emit(notification) }
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

    fun clearSelectedTasks() {
        sendEvent(ClearTaskSelection)
    }

    fun addSelectedTasksToScope(scopeId: Long) {
        sendEvent(MoveSelectedTasksToScope(scopeId))
    }

    fun removeSelectedTaskFromScope() {
        sendEvent(DeleteSelectedTasksFromScope)
    }

    fun scopeIndexChanged(index: Int) {
        // TODO: emit changed scope index
//        launch { eventsFlow.emit(ScopeSelected(scopeId)) }
    }

}


