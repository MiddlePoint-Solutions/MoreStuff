package co.softov.morestuff.android.ui.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.Filter
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithOneTimeScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithReminderScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTasksUseCase: SearchTasksUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getActiveTasksWithOneTimeScheduleUseCase: GetActiveTasksWithOneTimeScheduleUseCase,
    private val getActiveTasksWithReminderScheduleUseCase: GetActiveTasksWithReminderScheduleUseCase,
    private val timeFormatter: TimeFormatter,
) : NoStateViewModel() {

    val searchResults: MutableState<List<TaskDomain>> = mutableStateOf(listOf())
    private val completedTasks: MutableState<List<TaskDomain>> = mutableStateOf(listOf())
    private val activeTasksWithSchedule: MutableState<List<TaskDomain>> = mutableStateOf(listOf())
    private val activeTasksWithReminder: MutableState<List<TaskDomain>> = mutableStateOf(listOf())

    var currentFilter: Filter = Filter.None
        set(value) {
            field = value
            loadTasksByFilter()
        }

    fun setFilter(filter: Filter) {
        currentFilter = filter
    }

    fun searchTasks(searchText: String) {
        val selectedTaskList = when (currentFilter) {
            Filter.Done -> completedTasks.value
            Filter.Reminder -> activeTasksWithReminder.value
            Filter.Scheduled -> activeTasksWithSchedule.value
            Filter.None -> listOf()
        }

        if (searchText.isEmpty()) {
            searchResults.value = selectedTaskList
        } else {
            if (currentFilter == Filter.None) {
                searchTasksUseCase(searchText)
                    .onEach { results -> searchResults.value = results }
                    .launchIn(viewModelScope)
            } else {
                searchResults.value = selectedTaskList.filter { task ->
                    task.title.contains(searchText, ignoreCase = true)
                }
            }
        }
    }

    private fun loadTasksByFilter() {
        when (currentFilter) {
            Filter.Done -> loadCompletedTasks()
            Filter.Reminder -> loadTasksWithReminderSchedule()
            Filter.Scheduled -> loadActiveTasksWithOneTimeSchedule()
            Filter.None -> clearResults()
        }
    }

    private fun loadCompletedTasks() {
        viewModelScope.launch {
            getCompletedTasksUseCase()
                .collect { results ->
                    val formattedTasks = results.map { task ->
                        task.copy(completeTime = timeFormatter.formatTimeDayMonthHour(task.completeTime))
                    }
                    completedTasks.value = formattedTasks
                    searchResults.value = formattedTasks
                }
        }
    }


    private fun loadActiveTasksWithOneTimeSchedule() {
        viewModelScope.launch {
            val result = getActiveTasksWithOneTimeScheduleUseCase()
            if (result is Either.Right) {
                activeTasksWithSchedule.value = result.value
                searchResults.value = activeTasksWithSchedule.value
            }
        }
    }

    private fun loadTasksWithReminderSchedule() {
        viewModelScope.launch {
            val result = getActiveTasksWithReminderScheduleUseCase()
            if (result is Either.Right) {
                activeTasksWithReminder.value = result.value
                searchResults.value = activeTasksWithReminder.value
            }
        }
    }

    private fun clearResults() {
        searchResults.value = listOf()
    }
}


