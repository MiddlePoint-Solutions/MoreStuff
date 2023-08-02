package co.softov.morestuff.android.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.FilterType
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTasksUseCase: SearchTasksUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase,
    private val timeFormatter: TimeFormatter,
) : NoStateViewModel() {

    var query by mutableStateOf("")
        private set
    var filter by mutableStateOf(FilterType.None)
        private set

    val searchResults = MutableStateFlow<List<TaskDomain>>(listOf())

    private var searchJob: Job? = null

    init {
        setSearchFilter(FilterType.None)
    }

    fun setSearchFilter(selected: FilterType) {
        filter = if (filter != selected) selected else FilterType.None
        loadTasksByFilter(query)
    }

    fun setSearchQuery(searchQuery: String) {
        this.query = searchQuery
        loadTasksByFilter(searchQuery)
    }

    fun clearSearchQuery() {
        setSearchQuery("")
    }

    fun reset() {
        clearSearchQuery()
        setSearchFilter(FilterType.None)
    }

    private fun loadTasksByFilter(searchText: String = "") {
        searchJob?.cancel()
        when (filter) {
            FilterType.Done -> loadCompletedTasks(searchText)
            FilterType.Reminder -> loadTasksWithReminderSchedule(searchText)
            FilterType.Scheduled -> loadActiveTasksWithOneTimeSchedule(searchText)
            FilterType.None -> searchTasksWithNoFilter(searchText)
        }
    }


    private fun searchTasksWithNoFilter(searchText: String) {
        searchJob = viewModelScope.launch {
            searchTasksUseCase(searchText)
                .onEach { results ->
                    searchResults.value = results
                }.launchIn(this)
        }
    }

    private fun loadCompletedTasks(searchText: String) {
        searchJob = viewModelScope.launch {
            getCompletedTasksUseCase()
                .collect { results ->
                    val formattedTasks = results.map { task ->
                        task.copy(completeTime = timeFormatter.formatTimeDayMonthHour(task.completeTime))
                    }.filter { task ->
                        task.title.contains(searchText, ignoreCase = true)
                    }
                    searchResults.value = formattedTasks
                }
        }
    }

    private fun loadActiveTasksWithOneTimeSchedule(searchText: String) {
        searchJob = viewModelScope.launch {
            getActiveTasksWithScheduleUseCase(listOf(ScheduleType.OneTime)).map {
                searchResults.value = it.filter { task ->
                    task.title.contains(searchText, ignoreCase = true)
                }
            }
        }
    }

    private fun loadTasksWithReminderSchedule(searchText: String) {
        searchJob = viewModelScope.launch {
            getActiveTasksWithScheduleUseCase(listOf(ScheduleType.Reminder)).map {
                searchResults.value = it.filter { task ->
                    task.title.contains(searchText, ignoreCase = true)
                }
            }
        }
    }

}



