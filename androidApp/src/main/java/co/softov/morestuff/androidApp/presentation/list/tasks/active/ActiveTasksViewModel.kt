package co.softov.morestuff.androidApp.presentation.list.tasks.active

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.androidApp.domain.usecase.task.GetActiveTasks
import co.softov.morestuff.androidApp.presentation.list.BaseListViewModel
import co.softov.morestuff.androidApp.presentation.list.tasks.TaskListViewEvent
import co.softov.morestuff.androidApp.presentation.list.tasks.TaskListViewEvent.UpdateTasks
import co.softov.morestuff.androidApp.presentation.list.tasks.TaskListViewState
import co.softov.morestuff.androidApp.presentation.list.tasks.model.TaskListItemMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ActiveTasksViewModel(
    private val getActiveTasks: GetActiveTasks
) : BaseListViewModel<TaskListViewState, TaskListViewEvent>(
    TaskListViewState()
) {

    private val taskListItemMapper = TaskListItemMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onLoadData() {
        viewModelScope.launch {
            getActiveTasks().map { flow ->
                flow.onEach {
                    val presentationViewModel = taskListItemMapper.map(it)
                    sendEvent(UpdateTasks(presentationViewModel))
                }.launchIn(this)
            }
        }
    }

    override fun onReduceState(event: TaskListViewEvent): TaskListViewState {
        return when (event) {
            is UpdateTasks -> state.copy(data = event.data)
        }
    }
}

