package co.softov.morestuff.androidApp.presentation.dashboard

import co.softov.morestuff.androidApp.app.presentation.navigation.BaseConductor
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.androidApp.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.androidApp.domain.enums.Priority
import co.softov.morestuff.androidApp.presentation.dashboard.options.OptionListItemViewModel
import co.softov.morestuff.androidApp.presentation.dashboard.options.TimeOption

data class DashboardViewState(
    val priority: Priority = Priority.Today(),
    val currentItemId: Long = 0L,
    val items: List<OptionListItemViewModel> = listOf()
) : BaseViewState

sealed class DashboardViewEvent : BaseViewEvent {
    object Init : DashboardViewEvent()
    class SetCurrentOption(val option: TimeOption) : DashboardViewEvent()
    class SetCustomTime(val time: Long) : DashboardViewEvent()
    class ChangePriority(val priority: Priority) : DashboardViewEvent()
}

interface DashboardConductor : BaseConductor {
    fun showTaskList()
    fun showTodayTimePicker()
    fun showTomorrowTimePicker()
    fun showDateTimePicker()
}