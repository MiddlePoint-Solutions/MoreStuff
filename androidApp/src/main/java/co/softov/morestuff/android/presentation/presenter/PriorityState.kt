package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel

data class PriorityViewState(
    val items: List<ScheduleListItemViewModel> = listOf()
) : BaseViewState

sealed class PriorityViewEvent : BaseViewEvent {

}


