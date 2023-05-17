package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.ReviewAction
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.usecase.task.GetReviewTaskUseCase
import co.softov.morestuff.android.domain.usecase.time.TimeFormatter
import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.presentation.presenter.ReviewRound.Final
import co.softov.morestuff.android.presentation.presenter.ReviewRound.Priority
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.OnDone
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.OnHighPriority
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.OnLowPriority
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.OnTomorrow
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.SetupInitialRound
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.SetupRound
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.Undo
import co.softov.morestuff.android.ui.list.model.TaskListItemMapper
import co.softov.morestuff.android.ui.list.model.TaskListItemViewModel
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewViewModel(
    private val getTasksForReviewUseCase: GetReviewTaskUseCase,
    timeManager: TimeManager,
    timeFormatter: TimeFormatter
) : BaseViewModel<ReviewModel, ReviewViewEvent>(ReviewModel()) {

    private val mapper = TaskListItemMapper(timeFormatter, timeManager)
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
            val task = getTasksForReviewUseCase()
                .first() // Get the first emitted list from the flow
                .map(mapper::map) // Convert each TaskDomain to TaskListItemViewModel
            sendEvent(SetupInitialRound(round, task))
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

    fun undoTask(task: TaskListItemViewModel) {
        Timber.d("undoTask: $task")
        roundEndDelayJob?.cancel()
        sendEvent(Undo(task))
    }

    private fun confirmResults() {
        dispatchAppStoreAction(
            ReviewAction.TaskReviewResults(
                tomorrow = state.tomorrow.map { it.id },
                high = state.high.map { it.id },
                low = state.low.map { it.id },
                done = state.done.map { it.id },
            )
        )
    }

    fun onTaskSwiped(
        task: TaskListItemViewModel,
        direction: SwipeDirection,
        isLast: Boolean
    ) {
        Timber.d("onTaskSwiped: $isLast")
        val event = when (direction) {
            SwipeDirection.Left -> {
                dispatchAppStoreAction(TaskAction.DecreaseTaskPriorityScoreAction(listOf(task.id)))
                OnLowPriority(task)
            }
            SwipeDirection.Right -> {
                dispatchAppStoreAction(TaskAction.IncreaseTaskPriorityScoreAction(listOf(task.id)))
                OnHighPriority(task)
            }
            SwipeDirection.Up -> {
                dispatchAppStoreAction(TaskAction.CompleteTaskAction(task.id, true))
                OnDone(task)
            }
            SwipeDirection.Down -> OnTomorrow(task)
        }
        //sendEvent(event)


        if (isLast) {
            roundEndDelayJob = viewModelScope.launch {
                delay(500)
                //sendEvent(SetupRound(Final))
                navigateBack()
                confirmResults()
            }
        }
    }
}