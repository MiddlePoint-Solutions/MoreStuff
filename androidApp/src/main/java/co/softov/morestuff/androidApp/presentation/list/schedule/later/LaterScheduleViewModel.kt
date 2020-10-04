package co.softov.morestuff.androidApp.presentation.list.schedule.later

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetSchedulesWithTitle
import co.softov.morestuff.androidApp.domain.usecase.schedule.RescheduleTask
import co.softov.morestuff.androidApp.domain.usecase.task.SetTaskComplete
import co.softov.morestuff.androidApp.presentation.list.BaseListViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewEvent
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewEvent.UpdateSchedule
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewState
import co.softov.morestuff.androidApp.presentation.list.schedule.model.ScheduleListItemMapper

class LaterScheduleViewModel(
    private val getSchedulesWithTitle: GetSchedulesWithTitle,
    rescheduleTask: RescheduleTask,
    setTaskComplete: SetTaskComplete
) : BaseListViewModel<ScheduleListViewState, ScheduleListViewEvent>(
    rescheduleTask,
    setTaskComplete,
    ScheduleListViewState()
) {

    private val scheduleItemMapper = ScheduleListItemMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onLoadData() {
        viewModelScope.launch {
            getSchedulesWithTitle().map { flow ->
                flow.onEach {
                    val schedule =
                        it.filter { scheduleWithTitle ->
                            scheduleWithTitle.scheduleTime == 0L
                        }.let { filter ->
                            scheduleItemMapper.map(filter)
                        }

                    sendEvent(UpdateSchedule(schedule))
                }.launchIn(this)
            }
        }
    }

    override fun onReduceState(event: ScheduleListViewEvent): ScheduleListViewState {
        return when (event) {
            is UpdateSchedule -> state.copy(data = event.data)
        }
    }
}

