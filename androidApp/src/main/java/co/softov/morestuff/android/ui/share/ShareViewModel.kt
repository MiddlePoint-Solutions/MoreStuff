package co.softov.morestuff.android.ui.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.scopeAll
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareViewModel(
    getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val searchTasksUseCase: SearchTasksUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val taskUiMapper: TaskUiMapper,
) : NoStateViewModel() {

    var query by mutableStateOf("")
        private set

    val tasks = getActiveTasksFlowUseCase(scopeAll.id)
        .map { taskUiMapper.map(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf()
        )

    private val searchQueryFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults = searchQueryFlow
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { searchTasksUseCase(it, true) }
        .map { taskUiMapper.map(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = listOf()
        )

    fun updateSearchQuery(searchQuery: String) {
        searchQueryFlow.update { searchQuery }
    }

    fun resetSearchQuery() {
        viewModelScope.launch {
            delay(300)
            searchQueryFlow.update { "" }
        }
    }

    /**
     * Creates a new task and returns its id.
     * This is only used when we need the taskId for navigation.
     *
     * @return TaskId of the newly created task
     */
    suspend fun createNewShareableTask(title: String, priority: Priority): Long {
        val params = TaskParams(title, priority, TaskType.User)
        val task = createTaskUseCase(params)
        dispatchSuspend(TaskAction.TaskCreatedAction(task, priority))
        return task.id
    }

}
