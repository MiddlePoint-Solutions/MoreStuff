package co.softov.morestuff.android.ui.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.usecase.task.CreateTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.TaskParams
import co.softov.morestuff.android.ui.model.NotificationState
import co.softov.morestuff.android.ui.model.NotificationState.None
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareViewModel(
    private val getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val taskUiMapper: TaskUiMapper,
) : NoStateViewModel() {
    var query by mutableStateOf("")
        internal set

    val filteredTasks = MutableStateFlow<List<TaskUiModel>>(listOf())
    private val selectedScopeId = MutableStateFlow<Long>(1)
    private var filterJob: Job? = null
    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
        filterTasks()
    }

    fun updateQuery(searchQuery: String) {
        this.query = searchQuery
        filterTasks()
    }

    private fun filterTasks() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            getActiveTasksFlowUseCase(selectedScopeId.value)
                .onEach { results ->
                    filteredTasks.update {
                        val filteredResults = results.filter { task ->
                            task.title.contains(query, ignoreCase = true)
                        }
                        taskUiMapper.map(filteredResults)
                    }
                }.launchIn(this)
        }
    }

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    var notification: NotificationState by mutableStateOf(None)
        private set

    override fun onLoadData() {
        getActiveTasksFlowUseCase(selectedScopeId.value)
            .onEach { tasks = it }
            .launchIn(viewModelScope)
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
