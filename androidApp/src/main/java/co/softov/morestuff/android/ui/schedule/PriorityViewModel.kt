package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.ReorderTaskUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent.*
import co.softov.morestuff.android.presentation.presenter.PriorityViewState
import co.softov.morestuff.android.ui.Screens
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class PriorityViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val reorderTaskUseCase: ReorderTaskUseCase,
) : NoStateViewModel() {

    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
    }

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    private var lastChange = 0 to 0

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onLoadData() {
        getActiveTasksUseCase()
            .mapLatest { tasks = it }
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
            delay(300)
            tasks = tasks.toMutableList().apply {
                remove(item)
            }
        }.invokeOnCompletion {
            dispatchAppStoreAction(TaskAction.CompleteTaskAction(item.id, true))
        }
    }

    fun showTaskChat(taskId: Long) {
        router.navigateTo(Screens.taskChat(taskId))
    }

}
