package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.model.TaskDomain
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
) : BaseViewModel<PriorityViewState, PriorityViewEvent>(PriorityViewState()) {

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
            .mapLatest {
                tasks = it
            }.launchIn(viewModelScope)
    }

    override fun onReduceState(event: PriorityViewEvent): PriorityViewState {
        return when (event) {
            is InitPriorityState -> state.copy(
                items = event.items
            )

            is ReorderItem -> state.copy(
                items = state.items.toMutableList().apply {
                    add(event.toPosition, removeAt(event.fromPosition))
                }
            )

            is CompleteItem -> state.copy(
                items = state.items.toMutableList().apply {
                    remove(event.item)
                }
            )
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
            Timber.d("lastChange: $lastChange")
            val task1 = tasks[lastChange.first]
            val task2 = tasks[lastChange.second]
            viewModelScope.launch {
                reorderTaskUseCase(task1.id, task2.id, task2.priorityScore)
            }
        }
    }


    fun completeTask(item: TaskDomain) {
        viewModelScope.launch {
            delay(300)
            sendEvent(CompleteItem(item))
        }
    }

    fun showTaskChat(taskId: Long) {
        router.navigateTo(Screens.taskChat(taskId))
    }

}
