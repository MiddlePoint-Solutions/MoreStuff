package co.softov.morestuff.androidApp.presentation.list.schedule.tomorrow

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.androidApp.domain.usecase.schedule.GetTomorrowSchedulesWithTitle
import co.softov.morestuff.androidApp.presentation.list.BaseListViewModel
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewEvent
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewEvent.UpdateSchedule
import co.softov.morestuff.androidApp.presentation.list.schedule.ScheduleListViewState
import co.softov.morestuff.androidApp.presentation.list.schedule.model.ScheduleListItemMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TomorrowScheduleViewModel(
    private val getSchedulesWithTitle: GetTomorrowSchedulesWithTitle
) : BaseListViewModel<ScheduleListViewState, ScheduleListViewEvent>(
    ScheduleListViewState()
) {

    private val scheduleItemMapper = ScheduleListItemMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onLoadData() {
        viewModelScope.launch {
            getSchedulesWithTitle()
                .onEach { sendEvent(UpdateSchedule(scheduleItemMapper.map(it))) }
                .launchIn(this)
        }
    }

    override fun onReduceState(event: ScheduleListViewEvent): ScheduleListViewState {
        return when (event) {
            is UpdateSchedule -> state.copy(data = event.data)
        }
    }
}

