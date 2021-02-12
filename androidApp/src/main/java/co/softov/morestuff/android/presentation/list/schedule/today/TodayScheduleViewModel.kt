package co.softov.morestuff.android.presentation.list.schedule.today

import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitle
import co.softov.morestuff.android.presentation.list.BaseListViewModel
import co.softov.morestuff.android.presentation.list.schedule.ScheduleListViewEvent
import co.softov.morestuff.android.presentation.list.schedule.ScheduleListViewEvent.UpdateSchedule
import co.softov.morestuff.android.presentation.list.schedule.ScheduleListViewState
import co.softov.morestuff.android.presentation.list.schedule.model.ScheduleListItemMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TodayScheduleViewModel(
    private val getSchedulesWithTitle: GetTodaySchedulesWithTitle
) : BaseListViewModel<ScheduleListViewState, ScheduleListViewEvent>(
    ScheduleListViewState()
) {

    private val scheduleItemMapper = ScheduleListItemMapper()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onLoadData() {
        launch {
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

