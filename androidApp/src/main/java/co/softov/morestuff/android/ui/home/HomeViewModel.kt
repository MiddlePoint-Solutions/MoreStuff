package co.softov.morestuff.android.ui.home

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.InsertTaskIntoScopeUseCase
import co.softov.morestuff.android.domain.usecase.task.RemoveTaskFromScopeUseCase
import co.softov.morestuff.android.ui.home.HomeUiEvent.AddSelectedTasksToScope
import co.softov.morestuff.android.ui.home.HomeUiEvent.ClearTaskSelection
import co.softov.morestuff.android.ui.home.HomeUiEvent.CompleteSelectedTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.CompleteTask
import co.softov.morestuff.android.ui.home.HomeUiEvent.DeleteSelectedTasks
import co.softov.morestuff.android.ui.home.HomeUiEvent.SetConfettiEnabled
import co.softov.morestuff.android.ui.home.HomeUiEvent.SetNotification
import co.softov.morestuff.android.ui.home.HomeUiEvent.ToggleTaskSelection
import co.softov.morestuff.android.ui.home.HomeUiEvent.UndoComplete
import co.softov.morestuff.android.ui.model.NotificationState.Complete
import co.softov.morestuff.android.ui.model.NotificationState.None
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val insertTaskIntoScopeUseCase: InsertTaskIntoScopeUseCase,
    private val removeTaskFromScopeUseCase: RemoveTaskFromScopeUseCase,
    taskMapper: TaskUiMapper,
) : BaseViewModel<HomeUiModel, HomeUiEvent>(HomeUiModel()) {

    val tasks = MutableStateFlow<List<TaskUiModel>>(listOf())
    private val selectedScopeId = MutableStateFlow<Long>(1)

    init {
        loadData()

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
            is HomeUiEvent.DeleteSelectedTasksFromScope -> {
                val selectedTaskIds = state.selectedTaskIds
                viewModelScope.launch {
                    selectedTaskIds.forEach { taskId ->
                        removeTaskFromScopeUseCase(taskId)
                    }
                }
                state
            }

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

    fun moveToTop(taskId: Long) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(taskId, PriorityActionType.Now)
        )
    }

    fun moveToBottom(taskId: Long) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(taskId, PriorityActionType.Later)
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
        sendEvent(HomeUiEvent.DeleteSelectedTasksFromScope)
    }

}

