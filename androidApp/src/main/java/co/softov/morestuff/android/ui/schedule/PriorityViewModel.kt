package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
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
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val reorderTaskUseCase: ReorderTaskUseCase,
) : NoStateViewModel() {

    override val enableDebug: Boolean
        get() = false

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    var notification: NotificationState by mutableStateOf(None)
        private set

    val model = MutableStateFlow(PriorityViewState())

    init {
        loadData()
    }

    private var lastChange = 0 to 0
    private var lastCompleted: TaskDomain? = null

    override fun onLoadData() {
        getActiveTasksUseCase()
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
                reorderTaskUseCase(task.id, task.priorityScore, scoreAbove, scoreBelow)
            }
        }
    }

    fun completeTask(item: TaskDomain) {
        viewModelScope.launch {
            delay(120)
            lastCompleted = item
            tasks = tasks.toMutableList().apply {
                remove(item)
            }
            dispatchAppStoreAction(TaskAction.CompleteTaskAction(item.id, true))
            notification = Complete
        }
    }

    fun toggleReminder(item: TaskDomain) {
        when (val schedule = item.activeSchedule) {
            null -> dispatchAppStoreAction(ScheduleAction.CreateReminderScheduleAction(item.id))
            else -> {
                if (schedule.scheduleType == ScheduleType.Reminder) {
                    dispatchAppStoreAction(ScheduleAction.CancelReminderScheduleAction(item.id))
                }
            }
        }
    }

    fun undoLastCompleted() {
        resetNotification()
        lastCompleted?.let { task ->
            dispatchAppStoreAction(TaskAction.CompleteTaskAction(task.id, false))
            lastCompleted = null
        }
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

}
