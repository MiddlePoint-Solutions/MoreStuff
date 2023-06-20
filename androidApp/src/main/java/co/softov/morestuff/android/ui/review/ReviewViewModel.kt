package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.usecase.task.GetReviewTaskUseCase
import co.softov.morestuff.android.domain.enums.ReviewActionType
import co.softov.morestuff.android.domain.redux.middleware.ReviewAction

import co.softov.morestuff.android.domain.usecase.task.UpdatePlanTaskPriorityUseCase

import co.softov.morestuff.android.domain.redux.middleware.TaskAction

import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.presentation.presenter.ReviewRound.Final
import co.softov.morestuff.android.presentation.presenter.ReviewRound.Priority
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.ItemReview
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.SetupInitialRound
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.SetupRound
import co.softov.morestuff.android.presentation.presenter.ReviewViewEvent.Undo
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.model.map.ReviewItemMapper
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewViewModel(
    private val getTasksForReviewUseCase: GetReviewTaskUseCase,
    private val reviewItemMapper: ReviewItemMapper,
    private val updatePlanTaskPriorityUseCase: UpdatePlanTaskPriorityUseCase
) : BaseViewModel<ReviewModel, ReviewViewEvent>(ReviewModel()) {

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
            ReviewModel(items = event.items)
        }

        is SetupRound -> when (event.round) {
            Priority -> ReviewModel(
                round = Priority,
                items = state.items
            )

            Final -> state.copy(round = Final)
        }

        is ItemReview -> state.copy(
            actions = state.actions.toMutableList().apply {
                add(event.item to event.action)
            }
        )

        is Undo -> state.copy(
            actions = state.actions.toMutableList().apply {
                filter { it.first.id != event.item.id }
            }
        )

    }

    private fun setInitialState(round: ReviewRound = Priority) {
        viewModelScope.launch {
            val task = getTasksForReviewUseCase()
                .first()
                .shuffled()
                .map(reviewItemMapper::map)
            sendEvent(SetupInitialRound(round, task))
        }
    }

    fun reset() {
        setInitialState()
    }

    fun undo(item: ReviewItemUiModel) {
        Timber.d("undoTask: $item")
        roundEndDelayJob?.cancel()
        dispatchAppStoreAction(ReviewAction.UndoReviewTaskUpdateAction(item.id, item.priorityScore))
        sendEvent(Undo(item))
    }

    fun onTaskSwiped(
        item: ReviewItemUiModel,
        direction: SwipeDirection,
        isLast: Boolean
    ) {
        Timber.d("onTaskSwiped: $isLast")
        val reviewAction = when (direction) {
            SwipeDirection.Left -> ReviewActionType.Less
            SwipeDirection.Right -> ReviewActionType.More
            SwipeDirection.Up -> ReviewActionType.Now
            SwipeDirection.Down -> ReviewActionType.Later
        }

        dispatchAppStoreAction(ReviewAction.ReviewPriorityScoreUpdateAction(item.id, reviewAction))
        sendEvent(ItemReview(item, reviewAction))

        if (isLast) {
            roundEndDelayJob = viewModelScope.launch {

                delay(500)
                navigateBack()
                updatePlanTaskPriorityUseCase.invoke()
                sendEvent(SetupRound(Final)) // TODO(Joseph) This would be used with compose navigation.
            }
        }
    }
    fun completeTask(item: ReviewItemUiModel) {
        dispatchAppStoreAction(TaskAction.CompleteTaskAction(item.id, true))
    }

}