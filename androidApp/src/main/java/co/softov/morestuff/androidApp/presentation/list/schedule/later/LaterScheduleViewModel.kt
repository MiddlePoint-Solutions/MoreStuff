package co.softov.morestuff.androidApp.presentation.list.schedule.later

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetLaterSchedulesWithTitle
import co.softov.morestuff.androidApp.presentation.list.BaseListViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewEvent
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewEvent.UpdateSchedule
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewState
import co.softov.morestuff.androidApp.presentation.list.schedule.model.ScheduleListItemMapper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LaterScheduleViewModel(
    private val getLaterSchedules: GetLaterSchedulesWithTitle
) : BaseListViewModel<ScheduleListViewState, ScheduleListViewEvent>(
    ScheduleListViewState()
) {

    private val scheduleItemMapper = ScheduleListItemMapper()

    override fun onLoadData() {
        viewModelScope.launch {
            getLaterSchedules()
                .onEach {
                    sendEvent(UpdateSchedule(scheduleItemMapper.map(it)))
                }
                .launchIn(this)
        }
    }

    override fun onReduceState(event: ScheduleListViewEvent): ScheduleListViewState {
        return when (event) {
            is UpdateSchedule -> state.copy(data = event.data)
        }
    }
}

