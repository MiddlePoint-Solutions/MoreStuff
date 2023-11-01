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
import co.softov.morestuff.android.ui.schedule.NotificationState
import co.softov.morestuff.android.ui.schedule.NotificationState.None
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ShareViewModel(
    private val getActiveTasksFlowUseCase: GetActiveTasksFlowUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
) : NoStateViewModel() {
    var query by mutableStateOf("")
        internal set

    val filteredTasks = MutableStateFlow<List<TaskDomain>>(listOf())

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
            getActiveTasksFlowUseCase()
                .onEach { results ->
                    val filteredResults = results.filter { task ->
                        task.title.contains(query, ignoreCase = true)
                    }
                    filteredTasks.value = filteredResults
                }.launchIn(this)
        }
    }

    var tasks: List<TaskDomain> by mutableStateOf(listOf())
        private set

    var notification: NotificationState by mutableStateOf(None)
        private set

    override fun onLoadData() {
        getActiveTasksFlowUseCase()
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
        store.dispatchSuspend(TaskAction.TaskCreatedAction(task, priority))
        return task.id
    }

}
