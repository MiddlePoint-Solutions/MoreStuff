package co.softov.morestuff.android.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.usecase.schedule.GetLaterSchedulesWithTitle
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitle
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitle
import co.softov.morestuff.android.domain.usecase.schedule.GetTomorrowSchedulesWithTitle
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasks
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasks
import co.softov.morestuff.android.presentation.list.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class SchedulePageViewModel(
    private val page: Page,
    private val getSchedules: GetSchedulesWithTitle,
    private val getLaterSchedules: GetLaterSchedulesWithTitle,
    private val getTodaySchedules: GetTodaySchedulesWithTitle,
    private val getTomorrowSchedules: GetTomorrowSchedulesWithTitle,
    private val getActiveTasks: GetActiveTasks,
    private val getCompleteTasks: GetCompletedTasks
) : ViewModel() {
    private val _state = MutableStateFlow(SchedulePageViewState())

    val state: StateFlow<SchedulePageViewState>
        get() = _state

    private val scheduleItemMapper by lazy { ScheduleListItemMapper() }
    private val taskListItemMapper by lazy { TaskListItemMapper() }

    init {
        Timber.d("Page: $page")
        viewModelScope.launch {
            when (page.type) {
                PageType.PAGE_ACTIVE_SCHEDULES -> {
                    getSchedules()
                        .map { scheduleItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(schedules = it) }
                        .launchIn(this)
                }
                PageType.PAGE_ACTIVE_TODAY -> {
                    getTodaySchedules()
                        .map { scheduleItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(schedules = it) }
                        .launchIn(this)
                }
                PageType.PAGE_ACTIVE_TOMORROW -> {
                    getTomorrowSchedules()
                        .map { scheduleItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(schedules = it) }
                        .launchIn(this)
                }
                PageType.PAGE_ACTIVE_LATER -> {
                    getLaterSchedules()
                        .map { scheduleItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(schedules = it) }
                        .launchIn(this)
                }
                PageType.PAGE_ACTIVE_TASKS -> {
                    getActiveTasks()
                        .map { taskListItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(tasks = it) }
                        .launchIn(this)
                }
                PageType.PAGE_COMPLETE_TASKS -> {
                    getCompleteTasks()
                        .map { taskListItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(tasks = it) }
                        .launchIn(this)
                }
            }
        }
    }

}

data class SchedulePageViewState(
    val schedules: List<ScheduleListItemViewModel> = emptyList(),
    val tasks: List<TaskListItemViewModel> = emptyList()
)

