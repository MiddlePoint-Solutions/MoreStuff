package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.ReorderTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskHasScheduleUseCase
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.schedule.NotificationState.Complete
import co.softov.morestuff.android.ui.schedule.NotificationState.None
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class PriorityViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val reorderTaskUseCase: ReorderTaskUseCase,
    private val taskHasScheduleUseCase: TaskHasScheduleUseCase,
) : NoStateViewModel() {

    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
    }

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    var notification: NotificationState by mutableStateOf(None)
        private set

    var tasksWithSchedule: List<Long> by mutableStateOf(listOf())
        private set

    private var lastChange = 0 to 0
    private var lastCompleted: TaskDomain? = null

    override fun onLoadData() {
        getActiveTasksUseCase()
            .onEach { tasks ->
                this.tasks = tasks
                tasksWithSchedule = tasks.filter { task -> taskHasScheduleUseCase(task.id) }.map { it.id }
            }
            .launchIn(viewModelScope)
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
            val taskId = tasks[toPosition].id
            val scoreAbove = tasks.getOrNull(toPosition - 1)?.priorityScore
            val scoreBelow = tasks.getOrNull(toPosition + 1)?.priorityScore
            viewModelScope.launch {
                reorderTaskUseCase(taskId, scoreAbove, scoreBelow)
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

    fun showTaskChat(taskId: Long) {
        router.navigateTo(Screens.taskChat(taskId))
    }

    fun undoLastCompleted() {
        resetNotification()
        lastCompleted?.let { task ->
            dispatchAppStoreAction(TaskAction.CompleteTaskAction(task.id, false))
            lastCompleted = null
        }
    }

    fun resetNotification() {
        notification = None
    }

}
