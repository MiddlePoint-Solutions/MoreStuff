package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulesForPriorityReviewUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityReviewModel
import co.softov.morestuff.android.presentation.presenter.PriorityReviewViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityReviewViewEvent.*
import co.softov.morestuff.android.presentation.presenter.PriorityRound
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.review.swipeable.Direction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PriorityReviewViewModel(
    private val getSchedulesForPriorityReviewUseCase: GetSchedulesForPriorityReviewUseCase
) : BaseViewModel<PriorityReviewModel, PriorityReviewViewEvent>(PriorityReviewModel()) {

    private val mapper = ScheduleListItemMapper()

    init {
        loadData()
    }

    override fun onLoadData() {
        setInitialState()
    }

    override fun onReduceState(event: PriorityReviewViewEvent) = when (event) {
        is SetupInitialRound -> {
            state.copy(
                round = event.round,
                roundNumber = state.roundNumber + 1,
                roundItems = event.items
            )
        }

        is SetupNextRound -> {
            val nextRound = when {
                state.highPriority.size >= NEXT_ROUND_MINIMUM -> PriorityRound.Next
                else -> PriorityRound.Final
            }
            state.copy(
                round = nextRound,
                roundNumber = state.roundNumber + 1,
                roundItems = state.highPriority,
                highPriority = listOf()
            )
        }

        is OnHighPriority -> state.copy(highPriority = state.highPriority + event.item)
        is OnLowPriority -> state.copy(lowPriority = state.lowPriority + event.item)
        is OnDone -> state.copy(done = state.done + event.item)
        is OnLater -> state.copy(later = state.later + event.item)
        is Undo -> state.copy(
            highPriority = state.highPriority - event.item,
            lowPriority = state.lowPriority - event.item,
            done = state.done - event.item,
            later = state.later - event.item,
        )
    }

    private fun setInitialState(round: PriorityRound = PriorityRound.Initial) {
        viewModelScope.launch {
            val schedules = getSchedulesForPriorityReviewUseCase().map(mapper::map).shuffled()
            sendEvent(SetupInitialRound(round, schedules))
        }
    }

    fun reset() {
        setInitialState(round = PriorityRound.Next)
    }

    fun undoTask(schedule: ScheduleListItemViewModel) {
        sendEvent(Undo(schedule))
    }

    fun onTaskSwiped(
        schedule: ScheduleListItemViewModel,
        direction: Direction,
        isLast: Boolean
    ) {
        val event = when (direction) {
            Direction.Left -> OnLowPriority(schedule)
            Direction.Right -> OnHighPriority(schedule)
            Direction.Up -> OnDone(schedule)
            Direction.Down -> OnLater(schedule)
        }
        sendEvent(event)

        if (isLast) {
            sendEvent(SetupNextRound)
        }
    }

    companion object {
        const val NEXT_ROUND_MINIMUM = 3
    }

}