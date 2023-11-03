package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.ReorderTaskUseCase
import co.softov.morestuff.android.ui.schedule.NotificationState.Complete
import co.softov.morestuff.android.ui.schedule.NotificationState.None
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PriorityViewModel(
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
) : NoStateViewModel() {

    override val enableDebug: Boolean
        get() = false

    val tasks = getActiveTasksFlowUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = listOf()
    )

    var notification: NotificationState by mutableStateOf(None)
        private set

    val model = MutableStateFlow(PriorityViewState())
    val selectedTaskIds = MutableStateFlow(listOf<Long>())
    private val recentlyCompletedTasks = mutableListOf<Long>()

    var showDeleteConfirmDialog: Boolean by mutableStateOf(false)
        private set

    init {
        loadData()
    }

    private val showContent = mutableStateOf(false)
    fun showContent() {
        showContent.value = true
    }

    override fun onAppStateChange(state: AppState) {
        model.update {
            it.copy(enableConfetti = state.settings.enableConfetti)
        }
    }

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            delay(120)
            saveToUndoList(listOf(taskId))
            dispatchAppStoreAction(TaskAction.CompleteTasksAction(listOf(taskId), true))
            notification = Complete
        }
    }

    private fun saveToUndoList(taskIds: List<Long>) {
        recentlyCompletedTasks.clear()
        recentlyCompletedTasks.addAll(taskIds.toList())
    }

    fun toggleReminder(task: TaskDomain) {
        when (task.hasReminder) {
            false -> dispatchAppStoreAction(ScheduleAction.CreateReminderScheduleAction(task.id))
            true -> dispatchAppStoreAction(ScheduleAction.CancelReminderScheduleAction(task.id))
        }
    }

    fun undoLastCompleted() {
        resetNotification()
        dispatchAppStoreAction(
            TaskAction.CompleteTasksAction(recentlyCompletedTasks, false)
        )
    }

    fun moveToTop(task: TaskDomain) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(task.id, PriorityActionType.Now)
        )
    }

    fun moveToBottom(task: TaskDomain) {
        dispatchAppStoreAction(
            PriorityAction.TaskPriorityUpdateAction(task.id, PriorityActionType.Later)
        )
    }

    fun resetNotification() {
        notification = None
    }

    fun completeSelectedTasks() {
        saveToUndoList(selectedTaskIds.value)
        selectedTaskIds.update { listOf() }
        dispatchAppStoreAction(TaskAction.CompleteTasksAction(recentlyCompletedTasks, true))
        notification = Complete
    }

    fun deleteSelectedTasks() {
        dispatchAppStoreAction(TaskAction.DeleteTasksAction(selectedTaskIds.value))
        showDeleteConfirmDialog = false
        selectedTaskIds.update { listOf() }
    }

    fun showDeleteDialog() {
        showDeleteConfirmDialog = true
    }

    fun dismissDeleteDialog() {
        showDeleteConfirmDialog = false
        deselectAllTasks()
    }

    fun toggleTaskSelection(taskId: Long) {
        selectedTaskIds.value = if (taskId in selectedTaskIds.value) {
            selectedTaskIds.value - taskId
        } else {
            selectedTaskIds.value + taskId
        }
        model.update {
            it.copy(taskSelectionEnabled = selectedTaskIds.value.isNotEmpty())
        }
    }

    fun deselectAllTasks() {
        selectedTaskIds.value = emptyList()
    }

}

