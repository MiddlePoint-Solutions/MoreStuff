package co.softov.morestuff.android.ui.home

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.ScopeAction
import co.softov.morestuff.android.domain.redux.state.TaskAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.InsertTaskIntoScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTaskFromScopeUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.*
import co.softov.morestuff.android.ui.model.NotificationState.Complete
import co.softov.morestuff.android.ui.model.NotificationState.None
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val insertTaskIntoScopeUseCase: InsertTaskIntoScopeUseCase,
    private val removeTaskFromScopeUseCase: RemoveTaskFromScopeUseCase,
    private val getScopesUseCase: GetScopesUseCase,
    taskMapper: TaskUiMapper,
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    val tasks = MutableStateFlow<List<TaskUiModel>>(listOf())
    val selectedScopeId = MutableStateFlow<Long>(1)
    private val _uiState = MutableStateFlow(HomeUiModel())
    val uiState: StateFlow<HomeUiModel> = _uiState.asStateFlow()

    init {
        loadData()

        viewModelScope.launch {
            val loadedScopes = getScopesUseCase()
            sendEvent(SetScopes(loadedScopes))
        }

        selectedScopeId.flatMapLatest { scopeId ->
            getActiveTasksFlowUseCase(scopeId)
        }
            .mapLatest { taskMapper.map(it, state.selectedTaskIds) }
            .onEach { tasks.value = it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    }


    override fun onAppStateChange(state: AppState) {
        sendEvent(SetConfettiEnabled(state.settings.enableConfetti))
    }

    override fun onReduceState(event: HomeUiEvent): HomeUiModel {
        return when (event) {
            ClearTaskSelection -> {
                tasks.update { it.map { task -> task.copy(isSelected = false) } }
                state.copy(
                    taskSelectionActive = false,
                    selectedTaskIds = listOf()
                )
            }

            UndoComplete -> with(state) {
                dispatchAppStoreAction(
                    TaskAction.CompleteTasksAction(recentlyCompletedTasks, false)
                )

                copy(
                    recentlyCompletedTasks = listOf(),
                    notification = None
                )
            }

            is CompleteTask -> with(state) {
                dispatchAppStoreAction(TaskAction.CompleteTasksAction(listOf(event.taskId), true))
                copy(
                    recentlyCompletedTasks = listOf(event.taskId),
                    notification = Complete
                )
            }

            CompleteSelectedTasks -> with(state) {
                dispatchAppStoreAction(TaskAction.CompleteTasksAction(selectedTaskIds, true))
                copy(
                    selectedTaskIds = listOf(),
                    taskSelectionActive = false,
                    recentlyCompletedTasks = selectedTaskIds,
                    notification = Complete
                )
            }

            DeleteSelectedTasks -> with(state) {
                dispatchAppStoreAction(TaskAction.DeleteTasksAction(selectedTaskIds))
                copy(
                    selectedTaskIds = listOf(),
                    taskSelectionActive = false
                )
            }

            is SetNotification -> state.copy(notification = event.notification)

            is ToggleTaskSelection -> with(state) {
                val newSelectedTaskIds = if (event.taskId in selectedTaskIds) {
                    selectedTaskIds - event.taskId
                } else {
                    selectedTaskIds + event.taskId
                }

                tasks.update {
                    it.map { task -> task.copy(isSelected = task.id in newSelectedTaskIds) }
                }

                copy(
                    taskSelectionActive = newSelectedTaskIds.isNotEmpty(),
                    selectedTaskIds = newSelectedTaskIds
                )
            }

            is AddSelectedTasksToScope -> {
                val selectedTaskIds = state.selectedTaskIds
                viewModelScope.launch {
                    selectedTaskIds.forEach { taskId ->
                        insertTaskIntoScopeUseCase(taskId, event.scopeId)
                    }
                }
                state
            }

            is DeleteSelectedTasksFromScope -> {
                val selectedTaskIds = state.selectedTaskIds
                viewModelScope.launch {
                    selectedTaskIds.forEach { taskId ->
                        removeTaskFromScopeUseCase(taskId, selectedScopeId.value)
                    }
                }
                state
            }

            is SetScopes -> state.copy(scopes = event.scopes)

            is CreateScope -> {
                createScope(event.uid, event.name)
                state
            }

            is SelectScope -> {
                selectScope(event.scopeId)
                state.copy(selectedScopeId = event.scopeId)
            }

            is DeleteScopes -> {
                deleteScopes(event.scopeIds)
                state
            }

            is UpdateScopeName -> {
                updateScopeName(event.scopeId, event.newName)
                state
            }

            is ReorderScopes -> {
                val reorderedScopes = state.scopes.sortedBy { scope ->
                    event.newOrder.indexOf(scope.scopeId)
                }
                state.copy(scopes = reorderedScopes)
            }

            is SetConfettiEnabled -> state.copy(confettiEnabled = event.enabled)
        }
    }

    fun handleEvent(event: HomeUiEvent) {
        when (event) {
            is CreateScope -> createScope(event.uid, event.name)
            is SelectScope -> selectScope(event.scopeId)
            is DeleteScopes -> deleteScopes(event.scopeIds)
            is UpdateScopeName -> updateScopeName(event.scopeId, event.newName)
            is ReorderScopes -> reorderScopes(event.newOrder)
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
                selectedScopeId.value
            )
        )
    }

    fun moveToBottom(taskId: Long) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(
                taskId,
                PriorityActionType.Later,
                selectedScopeId.value
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
        Timber.d("Scope seleccionado: $scopeId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedScopeId = scopeId)
            updateCurrentScopeId(scopeId)
        }
    }


    fun deleteScopes(scopeIds: List<Long>) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.DeleteScopeAction(scopeIds))
            delay(500)
        }
    }

    private fun updateScopeName(scopeId: Long, newName: String) {
        viewModelScope.launch {
            dispatchAppStoreAction(ScopeAction.UpdateScopeNameAction(scopeId, newName))
            delay(500)
        }
    }

    private fun reorderScopes(newOrder: List<Long>) {
        viewModelScope.launch {
            newOrder.forEachIndexed { index, scopeId ->
                dispatchAppStoreAction(ScopeAction.UpdateScopeOrderAction(scopeId, index.toLong()))
            }
            delay(500)
        }
    }

    private fun updateCurrentScopeId(newScopeId: Long) {
        selectedScopeId.value = newScopeId
    }

}

