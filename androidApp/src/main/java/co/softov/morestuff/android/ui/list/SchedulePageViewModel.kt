package co.softov.morestuff.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.usecase.schedule.GetLaterSchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTomorrowSchedulesWithTitleUseCase
import co.softov.morestuff.android.domain.usecase.task.GetActiveTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.list.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class SchedulePageViewModel(
    private val page: PageType,
    private val getSchedules: GetSchedulesWithTitleFlowUseCase,
    private val getLaterSchedules: GetLaterSchedulesWithTitleUseCase,
    private val getTodaySchedules: GetTodaySchedulesWithTitleUseCase,
    private val getTomorrowSchedules: GetTomorrowSchedulesWithTitleUseCase,
    private val getActiveTasksUseCase: GetActiveTasksUseCase,
    private val getCompleteTasks: GetCompletedTasksUseCase,
    private val timeFormatter: TimeFormatter,
    private val timeManager: TimeManager
) : ViewModel() {
    private val _state = MutableStateFlow(SchedulePageViewState())
    val state: StateFlow<SchedulePageViewState>
        get() = _state
    private val scheduleItemMapper by lazy { ScheduleListItemMapper() }
    private val taskListItemMapper by lazy { TaskListItemMapper(timeFormatter, timeManager) }

    init {
        Timber.d("Page: $page")
        viewModelScope.launch {
            when (page) {
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
                    getActiveTasksUseCase()
                        .map { taskListItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(tasks = it) }
                        .launchIn(this)
                }
                PageType.PAGE_COMPLETE_TASKS -> {
                    getCompleteTasks().onEach { Timber.d("ALEXXX: $it") }
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
    val tasks: List<TaskListItemViewModel> = emptyList(),
)

