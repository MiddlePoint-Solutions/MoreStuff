package co.softov.morestuff.android.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.task.DecreaseTaskPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.IncreaseTaskPriorityScoreUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent.*
import co.softov.morestuff.android.presentation.presenter.PriorityViewState
import co.softov.morestuff.android.ui.Screens
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class PriorityViewModel(
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val decreaseTaskPriorityScoreUseCase: DecreaseTaskPriorityScoreUseCase,
    private val increaseTaskPriorityScoreUseCase: IncreaseTaskPriorityScoreUseCase,
) : BaseViewModel<PriorityViewState, PriorityViewEvent>(PriorityViewState()) {

    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
    }

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

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

    fun reorderTaskItem(fromPosition: Int, toPosition: Int) {
        tasks = tasks.toMutableList().apply {
            add(toPosition, removeAt(fromPosition))
        }
    }


//TODO: reorder task items and add new score:

    /* fun reorderTaskItem(fromPosition: Int, toPosition: Int) {
            tasks = tasks.toMutableList().apply {
                val movedTask = removeAt(fromPosition)
                add(toPosition, movedTask)

               if (toPosition > fromPosition) {
                   viewModelScope.launch {
                        decreaseTaskPriorityScoreUseCase(movedTask.id)
                    }
                } else {
                    viewModelScope.launch {
                        increaseTaskPriorityScoreUseCase(movedTask.id)
                    }
                }
            }
        }*/


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
