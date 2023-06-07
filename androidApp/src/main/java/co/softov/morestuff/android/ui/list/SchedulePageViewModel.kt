package co.softov.morestuff.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.schedule.GetSchedulesWithTitleFlowUseCase
import co.softov.morestuff.android.domain.usecase.task.GetCompletedTasksUseCase
import co.softov.morestuff.android.domain.usecase.task.GetLaterTaskUseCase
import co.softov.morestuff.android.domain.usecase.task.GetNowTaskUseCase
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.ui.model.PageType
import co.softov.morestuff.android.ui.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.model.TaskListItemViewModel
import co.softov.morestuff.android.ui.model.map.ScheduleListItemMapper
import co.softov.morestuff.android.ui.model.map.TaskListItemMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class SchedulePageViewModel(
    private val page: PageType,
    private val getNowTaskUseCase: GetNowTaskUseCase,
    private val getLaterTaskUseCase: GetLaterTaskUseCase,
    private val getCompleteTasks: GetCompletedTasksUseCase,
    private val getSchedules: GetSchedulesWithTitleFlowUseCase,
    private val timeFormatter: TimeFormatter,
    private val timeManager: TimeManager,
) : ViewModel() {
    private val _state = MutableStateFlow(SchedulePageViewState())
    val state: StateFlow<SchedulePageViewState>
        get() = _state
    private val taskListItemMapper by lazy { TaskListItemMapper(timeFormatter, timeManager) }
    private val scheduleListItemMapper by lazy { ScheduleListItemMapper() }

    init {
        Timber.d("Page: $page")
        viewModelScope.launch {
            when (page) {

                PageType.PAGE_ACTIVE_NOW -> {
                    getNowTaskUseCase()
                        .map { taskListItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(tasks = it) }
                        .launchIn(this)
                }
                PageType.PAGE_ACTIVE_LATER -> {
                    getLaterTaskUseCase()
                        .map { taskListItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(tasks = it) }
                        .launchIn(this)
                }
                PageType.PAGE_SCHEDULE -> {
                    getSchedules()
                        .map { scheduleListItemMapper.map(it) }
                        .onEach { _state.value = SchedulePageViewState(schedules = it) }
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
    val tasks: List<TaskListItemViewModel> = emptyList(),
)

