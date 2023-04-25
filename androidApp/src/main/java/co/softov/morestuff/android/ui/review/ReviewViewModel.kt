package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.state.ReviewAction
import co.softov.morestuff.android.domain.usecase.priority.GetSchedulesForPriorityReviewUseCase
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.*
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.presentation.presenter.ReviewRound.*
import co.softov.morestuff.android.ui.list.model.ScheduleListItemMapper
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import timber.log.Timber

class ReviewViewModel(
    private val getSchedulesForPriorityReviewUseCase: GetSchedulesForPriorityReviewUseCase
) : BaseViewModel<ReviewModel, ReviewViewEvent>(ReviewModel()) {

    private val mapper = ScheduleListItemMapper()
    private var roundEndDelayJob: Job? = null

    override val enableDebug: Boolean
        get() = false

    init {
        loadData()
    }

    override fun onLoadData() {
        setInitialState()
    }

    override fun onReduceState(event: ReviewViewEvent) = when (event) {
        is SetupInitialRound -> {
            ReviewModel(
                round = event.round,
                roundNumber = 1,
                roundItems = event.items
            )
        }

        is SetupRound -> when (event.round) {
            Priority -> {
                ReviewModel(
                    round = Priority,
                    roundNumber = state.roundNumber + 1,
                    roundItems = state.roundItems
                )
            }

            Final -> {
                state.copy(
                    round = Final,
                    roundNumber = state.roundNumber + 1,
                    roundItems = state.high + state.low,
                )
            }
        }

        is OnHighPriority -> state.copy(high = state.high + event.item)
        is OnLowPriority -> state.copy(low = state.low + event.item)
        is OnDone -> state.copy(done = state.done + event.item)
        is OnTomorrow -> state.copy(tomorrow = state.tomorrow + event.item)
        is Undo -> state.copy(
            high = state.high - event.item,
            low = state.low - event.item,
            done = state.done - event.item,
            tomorrow = state.tomorrow - event.item,
        )
    }

    private fun setInitialState(round: ReviewRound = Priority) {
        viewModelScope.launch {
            val schedules = getSchedulesForPriorityReviewUseCase().map(mapper::map).shuffled()
            sendEvent(SetupInitialRound(round, schedules))
        }
    }

    fun reorderTaskItem(fromPosition: Int, toPosition: Int) {
        state = state.copy(
            roundItems = state.roundItems.toMutableList().apply {
                add(toPosition, removeAt(fromPosition))
            }
        )
    }

    fun reset() {
        setInitialState()
    }

    fun undoTask(schedule: ScheduleListItemViewModel) {
        Timber.d("undoTask: $schedule")
        roundEndDelayJob?.cancel()
        sendEvent(Undo(schedule))
    }

    private fun confirmResults() {
        dispatchAppStoreAction(
            ReviewAction.ScheduleReviewResults(
                tomorrow = state.tomorrow.map { it.taskId },
                high = state.high.map { it.taskId },
                low = state.low.map { it.taskId },
                done = state.done.map { it.taskId },
            )
        )
    }

    fun onTaskSwiped(
        schedule: ScheduleListItemViewModel,
        direction: SwipeDirection,
        isLast: Boolean
    ) {
        Timber.d("onTaskSwiped: $isLast")
        val event = when (direction) {
            SwipeDirection.Left -> OnLowPriority(schedule)
            SwipeDirection.Right -> OnHighPriority(schedule)
            SwipeDirection.Up -> OnDone(schedule)
            SwipeDirection.Down -> OnTomorrow(schedule)
        }
        sendEvent(event)

        if (isLast) {
            roundEndDelayJob = viewModelScope.launch {
                delay(1000)
                sendEvent(SetupRound(Final))
                confirmResults()
            }
        }
    }
}