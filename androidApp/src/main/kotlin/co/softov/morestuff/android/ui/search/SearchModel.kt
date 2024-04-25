package co.softov.morestuff.android.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import co.softov.morestuff.android.domain.enums.FilterType
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.ui.model.TaskUiModel
import co.softov.morestuff.android.ui.model.map.TaskUiMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun searchModel(
    initialState: SearchState,
    events: Flow<SearchEvent>,
    searchTasksUseCase: SearchTasksUseCase = koinInject(),
    getCompletedTasksUseCase: GetCompletedTasksUseCase = koinInject(),
    getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase = koinInject(),
    taskUiMapper: TaskUiMapper = koinInject()
): SearchState {
    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf(initialState) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(state.query, state.filter) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            loadTasksByFilter(state.query, state.filter, { result ->
                state = state.copy(searchResults = result)
            }, taskUiMapper, searchTasksUseCase, getCompletedTasksUseCase, getActiveTasksWithScheduleUseCase)
        }
    }

    LaunchedEffect(events) {
        events.collect { event ->
            state = when (event) {
                is SearchEvent.SetSearchQuery -> {
                    state.copy(query = event.query)
                }

                is SearchEvent.SetSearchFilter -> {
                    val newFilter = if (state.filter == event.filter) FilterType.None else event.filter
                    state.copy(filter = newFilter)
                }

                is SearchEvent.ClearSearchQuery -> {
                    state.copy(query = "")
                }

                is SearchEvent.ResetSearch -> {
                    state.copy(query = "", filter = FilterType.None)
                }
            }
        }
    }

    return state
}

suspend fun loadTasksByFilter(
    query: String,
    filter: FilterType,
    updateState: (List<TaskUiModel>) -> Unit,
    taskUiMapper: TaskUiMapper,
    searchTasksUseCase: SearchTasksUseCase,
    getCompletedTasksUseCase: GetCompletedTasksUseCase,
    getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase
) {
    when (filter) {
        FilterType.Done -> {
            val tasks = getCompletedTasksUseCase().first()
            updateState(taskUiMapper.map(tasks.filter { it.title.contains(query, ignoreCase = true) }))
        }
        FilterType.Reminder, FilterType.Scheduled -> {
            val scheduleTypes = if (filter == FilterType.Scheduled) listOf(ScheduleType.OneTime) else listOf(ScheduleType.Reminder)
            getActiveTasksWithScheduleUseCase(scheduleTypes).map { tasks ->
                val filteredTasks = taskUiMapper.map(tasks.filter { it.title.contains(query, ignoreCase = true) })
                updateState(filteredTasks)
            }
        }
        FilterType.None -> {
            val tasks = searchTasksUseCase(query, false).first()
            updateState(taskUiMapper.map(tasks.filter { it.title.contains(query, ignoreCase = true) }))
        }
    }
}




