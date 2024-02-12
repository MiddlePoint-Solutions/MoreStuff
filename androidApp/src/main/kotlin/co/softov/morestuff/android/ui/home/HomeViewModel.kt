package co.softov.morestuff.android.ui.home

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.CreateScopeUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.ClearTaskSelection
import co.softov.morestuff.android.ui.home.HomeUiEvent.CompleteSelectedTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.DeleteSelectedTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.MoveSelectedTasksToScope
import co.softov.morestuff.android.ui.home.HomeUiEvent.ScopeSelected
import co.softov.morestuff.android.ui.home.HomeUiEvent.SetConfettiEnabled
import co.softov.morestuff.android.ui.home.HomeUiEvent.ToggleTaskSelection
import co.softov.morestuff.android.ui.home.HomeUiEvent.UndoComplete
import co.softov.morestuff.android.ui.home.HomeUiEvent.UndoMoveTasks
import co.softov.morestuff.android.ui.model.NotificationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val createScopeUseCase: CreateScopeUseCase
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
                    TaskAction.UpdateTasksToScopeAction(selectedTasks, event.scopeId)
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
                this
            }

            is ScopeSelected -> copy(selectedScopeId = event.scopeId)

            is SetConfettiEnabled -> state.copy(confettiEnabled = event.enabled)

            is HomeUiEvent.CreateScopeForSelectedTasks -> {
                launch {
                    createScopeUseCase(event.title).onRight {
                        sendEvent(MoveSelectedTasksToScope(it.id))
                    }
                }
                state
            }
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

}


