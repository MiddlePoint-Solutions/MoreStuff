package co.softov.morestuff.android.presentation.list.schedule

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.presentation.list.schedule.model.ScheduleListItemViewModel

data class ScheduleListViewState(
    val data: List<ScheduleListItemViewModel> = listOf()
) : BaseViewState

sealed class ScheduleListViewEvent : BaseViewEvent {
    data class UpdateSchedule(val data: List<ScheduleListItemViewModel>) : ScheduleListViewEvent()
}