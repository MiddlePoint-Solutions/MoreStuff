package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.state.ReviewAction
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulesForPriorityReviewUseCase
import co.softov.morestuff.android.presentation.presenter.PriorityReviewModel
import co.softov.morestuff.android.presentation.presenter.PriorityReviewViewEvent
import co.softov.morestuff.android.presentation.presenter.PriorityReviewViewEvent.*
import co.softov.morestuff.android.presentation.presenter.PriorityRound
import co.softov.morestuff.android.presentation.presenter.PriorityRound.*
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.review.swipeable.Direction
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
            PriorityReviewModel(
                round = event.round,
                roundNumber = 1,
                roundItems = event.items
            )
        }

        is SetupNextRound -> when (state.round) {
            Today -> {
                if (state.today.size > FINAL_ROUND_MINIMUM) {
                    state.copy(
                        round = Now,
                        roundNumber = state.roundNumber + 1,
                        roundItems = state.today,
                    )
                } else {
                    state.copy(
                        round = Final,
                        roundNumber = state.roundNumber + 1,
                        roundItems = state.today,
                        now = state.today
                    )
                }
            }
            Now -> {
                if (state.now.size > FINAL_ROUND_MINIMUM) {
                    state.copy(
                        round = Now,
                        roundNumber = state.roundNumber + 1,
                        roundItems = state.now,
                        now = listOf()
                    )
                } else {
                    state.copy(
                        round = Final,
                        roundNumber = state.roundNumber + 1,
                        roundItems = state.now,
                        now = state.now
                    )
                }
            }
            else -> {
                state.copy(
                    round = Final,
                    roundNumber = state.roundNumber + 1,
                    roundItems = state.now,
                )
            }
        }

        is OnHighPriority -> state.apply {
            return if (round == Today) {
                copy(today = today + event.item)
            } else {
                copy(now = now + event.item)
            }
        }
        is OnLowPriority -> state.apply {
            return if (round == Today) {
                copy(tomorrow = tomorrow + event.item)
            } else {
                copy(snooze = snooze + event.item)
            }
        }
        is OnDone -> state.copy(done = state.done + event.item)
        is OnLater -> state.copy(later = state.later + event.item)
        is Undo -> state.copy(
            now = state.now - event.item,
            snooze = state.snooze - event.item,
            done = state.done - event.item,
            later = state.later - event.item,
            today = state.today - event.item,
            tomorrow = state.tomorrow - event.item,
        )
    }

    private fun setInitialState(round: PriorityRound = Today) {
        viewModelScope.launch {
            val schedules = getSchedulesForPriorityReviewUseCase().map(mapper::map).shuffled()
            sendEvent(SetupInitialRound(round, schedules))
        }
    }

    fun reset() {
        setInitialState()
    }

    fun undoTask(schedule: ScheduleListItemViewModel) {
        sendEvent(Undo(schedule))
    }

    fun confirmResults() {
        dispatchAppStoreAction(
            ReviewAction.ScheduleReviewResults(
                tomorrow = state.tomorrow.map { it.taskId },
                now = state.now.map { it.taskId },
                next = state.snooze.map { it.taskId },
                done = state.done.map { it.taskId },
                later = state.later.map { it.taskId },
            )
        )
        navigateBack()
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
        const val FINAL_ROUND_MINIMUM = 1
    }

}