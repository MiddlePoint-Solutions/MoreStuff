package co.softov.morestuff.android.ui.schedule

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleFlow
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityViewState
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PriorityViewModel(
    private val getTodaySchedules: GetTodaySchedulesWithTitleFlow,
) : BaseViewModel<PriorityViewState, PriorityViewEvent>(PriorityViewState()) {

    private val mapper = ScheduleListItemMapper()

    override val enableDebug: Boolean
        get() = false

    val tasks: StateFlow<List<ScheduleListItemViewModel>> =
        getTodaySchedules()
            .map(mapper::map)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = listOf()
            )

    init {
        loadData()
    }

    override fun onLoadData() {

    }

    override fun onReduceState(event: PriorityViewEvent): PriorityViewState {
        return super.onReduceState(event)
    }

}
