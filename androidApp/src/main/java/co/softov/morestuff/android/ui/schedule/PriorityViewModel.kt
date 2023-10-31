package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.MutableState
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class PriorityViewModel(
    private val getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val reorderTaskUseCase: ReorderTaskUseCase,
) : NoStateViewModel() {

    override val enableDebug: Boolean
        get() = false

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    var notification: NotificationState by mutableStateOf(None)
        private set

    val model = MutableStateFlow(PriorityViewState())
    var selectedTaskIds: MutableState<List<Long>> = mutableStateOf(listOf())
        private set

    private var recentlyCompletedTasks: MutableList<TaskDomain> = mutableListOf()
    val undoBulkMode = MutableStateFlow(false)

    init {
        loadData()
    }

    private var lastChange = 0 to 0
    private var lastCompleted: TaskDomain? = null

    private val showContent = mutableStateOf(false)
    fun showContent() {
        showContent.value = true
    }

    override fun onLoadData() {
        getActiveTasksFlowUseCase()
            .onEach { tasks = it }
            .launchIn(viewModelScope)
    }

    override fun onAppStateChange(state: AppState) {
        model.update {
            it.copy(enableConfetti = state.settings.enableConfetti)
        }
    }

    fun updateTaskOrder(fromPosition: Int, toPosition: Int) {
        tasks = tasks.toMutableList().apply {
            add(toPosition, removeAt(fromPosition))
        }
        lastChange = toPosition to fromPosition
    }

    fun reorderTaskItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition != toPosition) {
            Timber.d("lastChange $toPosition: ${tasks[toPosition].title}")
            val task = tasks[toPosition]
            val scoreAbove = tasks.getOrNull(toPosition - 1)?.priorityScore
            val scoreBelow = tasks.getOrNull(toPosition + 1)?.priorityScore
            viewModelScope.launch {
                reorderTaskUseCase(task.id, scoreAbove, scoreBelow)
            }
        }
    }

    fun completeTask(item: TaskDomain) {
        viewModelScope.launch {
            delay(120)
            recentlyCompletedTasks.add(item)
            tasks = tasks.toMutableList().apply {
                remove(item)
            }
            dispatchAppStoreAction(TaskAction.CompleteTaskAction(item.id, true))
            notification = Complete
        }
    }

    fun toggleReminder(task: TaskDomain) {
        when (task.hasReminder) {
            false -> dispatchAppStoreAction(ScheduleAction.CreateReminderScheduleAction(task.id))
            true -> dispatchAppStoreAction(ScheduleAction.CancelReminderScheduleAction(task.id))
        }
    }

    fun undoLastCompleted() {
        resetNotification()
        for (task in recentlyCompletedTasks) {
            dispatchAppStoreAction(TaskAction.CompleteTaskAction(task.id, false))
        }
        recentlyCompletedTasks.clear()
        undoBulkMode.value = true
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
        val taskIdsToComplete = mutableListOf<Long>()

        for (taskId in selectedTaskIds.value) {
            val taskToComplete = tasks.find { it.id == taskId }
            if (taskToComplete != null) {
                recentlyCompletedTasks.add(taskToComplete)
                completeTask(taskToComplete)
                taskIdsToComplete.add(taskId)
            }
        }
        selectedTaskIds.value = selectedTaskIds.value.filterNot { it in taskIdsToComplete }
    }
    fun deleteSelectedTasks() {
        viewModelScope.launch {
            for (taskId in selectedTaskIds.value) {
                dispatchAppStoreAction(TaskAction.DeleteTaskAction(taskId))
            }
            tasks = tasks.filterNot { it.id in selectedTaskIds.value }
            selectedTaskIds.value = emptyList()
        }
    }
    fun handleTaskSelection(task: TaskDomain) {
        if (task.id in selectedTaskIds.value) {
            selectedTaskIds.value = selectedTaskIds.value - task.id
        } else {
            selectedTaskIds.value = selectedTaskIds.value + task.id
        }
    }

    fun handleLongPressOnTask(task: TaskDomain) {
        selectedTaskIds.value = listOf(task.id) + selectedTaskIds.value
    }

}
