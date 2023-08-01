package co.softov.morestuff.android.ui.search

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import co.softov.morestuff.android.app.presentation.viewmodel.NoStateViewModel
import co.softov.morestuff.android.domain.enums.FilterName
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksWithScheduleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.SearchTasksUseCase
import co.softov.morestuff.android.domain.util.TimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class SearchViewModel(
    private val searchTasksUseCase: SearchTasksUseCase,
    private val getCompletedTasksUseCase: GetCompletedTasksUseCase,
    private val getActiveTasksWithScheduleUseCase: GetActiveTasksWithScheduleUseCase,
    private val timeFormatter: TimeFormatter,
) : NoStateViewModel() {

    val searchResults = MutableStateFlow<List<TaskDomain>>(listOf())

    val currentFilterName = MutableStateFlow(FilterName.None)

    init {
        viewModelScope.launch {
            currentFilterName.collectLatest {
                loadTasksByFilter()
            }
        }
    }

    fun searchTasks(searchText: String) {
        loadTasksByFilter(searchText)
    }

    private fun loadTasksByFilter(searchText: String = "") {
        viewModelScope.launch {
            when (currentFilterName.value) {
                FilterName.Done -> loadCompletedTasks(searchText)
                FilterName.Reminder -> loadTasksWithReminderSchedule(searchText)
                FilterName.Scheduled -> loadActiveTasksWithOneTimeSchedule(searchText)
                FilterName.None -> {
                    clearResults()
                    if (searchText.isNotEmpty()) {
                        searchTasksWithNoFilter(searchText)
                    }
                }
            }
        }
    }


    private fun searchTasksWithNoFilter(searchText: String) {
        viewModelScope.launch {
            searchTasksUseCase(searchText)
                .onEach { results ->
                    searchResults.value = results
                }.launchIn(viewModelScope)
        }
    }

    private suspend fun loadCompletedTasks(searchText: String) {
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

    private suspend fun loadActiveTasksWithOneTimeSchedule(searchText: String) {
        val result = getActiveTasksWithScheduleUseCase(listOf(ScheduleType.OneTime))
        if (result is Either.Right) {
            searchResults.value = result.value.filter { task ->
                task.title.contains(searchText, ignoreCase = true)
            }
        }
    }

    private suspend fun loadTasksWithReminderSchedule(searchText: String) {
        val result = getActiveTasksWithScheduleUseCase(listOf(ScheduleType.Reminder))
        if (result is Either.Right) {
            searchResults.value = result.value.filter { task ->
                task.title.contains(searchText, ignoreCase = true)
            }
        }
    }

    private fun clearResults() {
        searchResults.value = listOf()
    }

    fun reset() {
        clearResults()
        currentFilterName.value = FilterName.None
    }
}



