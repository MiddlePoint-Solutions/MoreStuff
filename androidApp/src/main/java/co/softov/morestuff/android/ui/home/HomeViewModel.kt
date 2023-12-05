package co.softov.morestuff.android.ui.home

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction
import co.softov.morestuff.android.domain.redux.state.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.InsertTaskIntoScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTaskFromScopeUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.*
import co.softov.morestuff.android.ui.model.NotificationState.Complete
import co.softov.morestuff.android.ui.model.NotificationState.None
import co.softov.morestuff.android.ui.model.TaskUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    getScopesFlowUseCase: GetScopesFlowUseCase,
    private val insertTaskIntoScopeUseCase: InsertTaskIntoScopeUseCase,
    private val removeTaskFromScopeUseCase: RemoveTaskFromScopeUseCase,
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    val selectedScopeId = MutableStateFlow<Long>(1)
    val selectedTasksFlow = MutableStateFlow<List<Long>>(listOf())

    private val _uiState = MutableStateFlow(HomeUiModel())


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
                selectedTasksFlow.update { listOf() }
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
                val completedTasks = selectedTasksFlow.getAndUpdate { listOf() }
                dispatchAppStoreAction(TaskAction.CompleteTasksAction(completedTasks, true))
                copy(
                    recentlyCompletedTasks = completedTasks,
                    notification = Complete
                )
            }

            DeleteSelectedTasks -> {
                val deletedTasks = selectedTasksFlow.getAndUpdate { listOf() }
                dispatchAppStoreAction(TaskAction.DeleteTasksAction(deletedTasks))
                this
            }

            is SetNotification -> copy(notification = event.notification)

            is ToggleTaskSelection -> {
                selectedTasksFlow.update {
                    if (event.taskId in it) it - event.taskId else it + event.taskId
                }
                this
            }

            is AddSelectedTasksToScope -> {
//                val selectedTaskIds = state.selectedTaskIds
//                viewModelScope.launch {
//                    selectedTaskIds.forEach { taskId ->
//                        insertTaskIntoScopeUseCase(taskId, event.scopeId)
//                    }
//                }
                state
            }

            is DeleteSelectedTasksFromScope -> {
//                val selectedTaskIds = state.selectedTaskIds
//                viewModelScope.launch {
//                    selectedTaskIds.forEach { taskId ->
//                        removeTaskFromScopeUseCase(taskId, selectedScopeId.value)
//                    }
//                }
                state
            }

            is CreateScope -> {
                createScope(event.uid, event.name)
                state
            }

            is SelectScope -> {
                selectScope(event.scopeId)
                state.copy(selectedScopeId = event.scopeId)
            }

            is DeleteScope -> {
                dispatchAppStoreAction(ScopeAction.DeleteScopeAction(event.scopeId))
                state
            }

            is UpdateScopeName -> {
                updateScopeName(event.scopeId, event.newName)
                state
            }

            is SetConfettiEnabled -> state.copy(confettiEnabled = event.enabled)
        }
    }

    fun handleEvent(event: HomeUiEvent) {
        when (event) {
            is CreateScope -> createScope(event.uid, event.name)
            is SelectScope -> selectScope(event.scopeId)
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

    fun updateTasksForSelectedScope(scopeId: Long) {
        selectedScopeId.value = scopeId
    }

    fun removeTaskFromScope() {
        sendEvent(DeleteSelectedTasksFromScope)
    }

    private fun createScope(uid: String, name: String) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.CreateScopeAction(uid, name))
            delay(500)
        }
    }

    private fun selectScope(scopeId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedScopeId = scopeId)
            updateCurrentScopeId(scopeId)
        }
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

    private fun updateCurrentScopeId(newScopeId: Long) {
        selectedScopeId.value = newScopeId
    }

}

