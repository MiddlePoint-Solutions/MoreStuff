package co.softov.morestuff.android.ui.schedule

import androidx.lifecycle.viewModelScope
import arrow.core.getOrElse
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.usecase.schedule.GetTodaySchedulesWithTitleUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityViewEvent.*
import co.softov.morestuff.android.presentation.presenter.PriorityViewState
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import kotlinx.coroutines.delay
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

            is ReorderItem -> state.copy(
                items = state.items.toMutableList().apply {
                    add(event.toPosition, removeAt(event.fromPosition))
                }
            )

            is CompleteItem -> state.copy(
                items = state.items.toMutableList().apply {
                    remove(event.item)
                }
            )
        }
    }

    fun reorderTaskItem(fromPosition: Int, toPosition: Int) {
        sendEvent(ReorderItem(fromPosition, toPosition))
    }

    fun completeTask(item: ScheduleListItemViewModel) {
        viewModelScope.launch {
            delay(300)
            sendEvent(CompleteItem(item))
        }
    }

}
