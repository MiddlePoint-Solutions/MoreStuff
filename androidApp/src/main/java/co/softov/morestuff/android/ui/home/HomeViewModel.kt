package co.softov.morestuff.android.ui.home

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.AddSelectedTasksToScope
import co.softov.morestuff.android.ui.home.HomeUiEvent.ClearTaskSelection
import co.softov.morestuff.android.ui.home.HomeUiEvent.CompleteSelectedTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.CompleteTask
import co.softov.morestuff.android.ui.home.HomeUiEvent.CreateScope
import co.softov.morestuff.android.ui.home.HomeUiEvent.DeleteScope
import co.softov.morestuff.android.ui.home.HomeUiEvent.DeleteSelectedTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.DeleteSelectedTasksFromScope
import co.softov.morestuff.android.ui.home.HomeUiEvent.ScopeSelected
import co.softov.morestuff.android.ui.home.HomeUiEvent.SetConfettiEnabled
import co.softov.morestuff.android.ui.home.HomeUiEvent.SetNotification
import co.softov.morestuff.android.ui.home.HomeUiEvent.ToggleTaskSelection
import co.softov.morestuff.android.ui.home.HomeUiEvent.UndoComplete
import co.softov.morestuff.android.ui.home.HomeUiEvent.UndoMoveTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.UpdateScopeName
import co.softov.morestuff.android.ui.model.NotificationState.Complete
import co.softov.morestuff.android.ui.model.NotificationState.None
import co.softov.morestuff.android.ui.model.NotificationState.TaskMovedToNewScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    getScopesFlowUseCase: GetScopesFlowUseCase,
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    val selectedTasks = MutableStateFlow<List<Long>>(listOf())

    val scopes = getScopesFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf(scopeAll)
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
                    TaskAction.AddTasksToScopeAction(
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

            is CreateScope -> {
                createScope(event.uid, event.name)
                this
            }

            is ScopeSelected -> copy(selectedScopeId = event.scopeId)

            is DeleteScope -> {
                dispatchAppStoreAction(ScopeAction.DeleteScopeAction(event.scopeId))
                this
            }

            is UpdateScopeName -> {
                updateScopeName(event.scopeId, event.newName)
                this
            }

            is SetConfettiEnabled -> state.copy(confettiEnabled = event.enabled)

            is ReorderScope -> {
                dispatchAppStoreAction((ScopeAction.UpdateScopeOrderAction(event.scopeId, event.newPosition)))
                this
            }
        }
    }

    fun handleEvent(event: HomeUiEvent) {
        when (event) {
            is CreateScope -> createScope(event.uid, event.name)
            is ScopeSelected -> selectScope(event.scopeId)
            is UpdateScopeName -> updateScopeName(event.scopeId, event.newName)
            else -> {}
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


    fun moveToTop(taskId: Long) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(
                taskId,
                PriorityActionType.Now,
            )
        )
    }

    fun moveToBottom(taskId: Long) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(
                taskId,
                PriorityActionType.Later,
            )
        )
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

    private fun createScope(uid: String, name: String) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.CreateScopeAction(uid, name))
            delay(500)
        }
    }

    fun selectScope(scopeId: Long) {
        Timber.d("Updating scopeId")
        sendEvent(ScopeSelected(scopeId))
    }

    fun deleteScopes(scopeId: Long) {
        sendEvent(DeleteScope(scopeId))
    }

    private fun updateScopeName(scopeId: Long, newName: String) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.UpdateScopeNameAction(scopeId, newName))
            delay(500)
        }
    }

    fun reorderScope(scopeId: Long, newPosition: Int) {
        viewModelScope.launch {
            sendEvent(ReorderScope(scopeId, newPosition))
        }
    }
}


