package co.softov.morestuff.android.ui.schedule

import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleFlowUseCase
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent.*
import co.softov.morestuff.android.presentation.presenter.PriorityViewState
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PriorityViewModel(
    private val getTodaySchedules: GetTodaySchedulesWithTitleUseCase,
) : BaseViewModel<PriorityViewState, PriorityViewEvent>(PriorityViewState()) {

    private val mapper = ScheduleListItemMapper()

    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
    }

    override fun onLoadData() {
        viewModelScope.launch {
            getTodaySchedules()
                .getOrElse { listOf() }
                .map(mapper::map)
                .also {
                    sendEvent(InitPriorityState(it))
                }
        }
    }

    override fun onReduceState(event: PriorityViewEvent): PriorityViewState {
        return when (event) {
            is InitPriorityState -> state.copy(
                items = event.items
            )

            is ReorderTask -> state.copy(
                items = state.items.toMutableList().apply {
                    add(event.toPosition, removeAt(event.fromPosition))
                }
            )
        }
    }

    fun reorderTaskItem(fromPosition: Int, toPosition: Int) {
        sendEvent(ReorderTask(fromPosition, toPosition))
    }

}
