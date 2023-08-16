package co.softov.morestuff.android.ui.review

import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.usecase.task.GetTasksWithoutScheduleUseCase
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction

import co.softov.morestuff.android.domain.redux.middleware.TaskAction

import co.softov.morestuff.android.presentation.presenter.ReviewModel
import co.softov.morestuff.android.presentation.presenter.ReviewRound
import co.softov.morestuff.android.presentation.presenter.ReviewRound.Final
import co.softov.morestuff.android.presentation.presenter.ReviewRound.Review
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
import kotlinx.coroutines.launch
import timber.log.Timber

class ReviewViewModel(
    private val getTasksWithoutScheduleUseCase: GetTasksWithoutScheduleUseCase,
    private val reviewItemMapper: ReviewItemMapper,
) : BaseViewModel<ReviewModel, ReviewViewEvent>(ReviewModel()) {

    private var roundEndDelayJob: Job? = null

    override val enableDebug: Boolean
        get() = false

    override fun onLoadData() {
        setInitialState()
    }

    override fun onReduceState(event: ReviewViewEvent) = when (event) {
        is SetupInitialRound -> {
            ReviewModel(items = event.items)
        }

        is SetupRound -> when (event.round) {
            Review -> ReviewModel(
                round = Review,
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

        is ReviewViewEvent.CompleteTask -> state.copy(
            items = state.items.filter { it.id != event.item.id }
        )


    }

    private fun setInitialState(round: ReviewRound = Review) {
        viewModelScope.launch {
            getTasksWithoutScheduleUseCase().map {
                val tasks = it.shuffled().map(reviewItemMapper::map)
                sendEvent(SetupInitialRound(round, tasks))
            }
        }
    }

    fun undo(item: ReviewItemUiModel) {
        Timber.d("undoTask: $item")
        roundEndDelayJob?.cancel()
        dispatchAppStoreAction(
            PriorityAction.UndoTaskPriorityUpdateAction(
                item.id,
                item.priorityScore
            )
        )
        sendEvent(Undo(item))
    }

    fun onTaskSwiped(
        item: ReviewItemUiModel,
        direction: SwipeDirection,
        isLast: Boolean,
    ) {
        Timber.d("onTaskSwiped: $isLast")
        val reviewAction = when (direction) {
            SwipeDirection.Left -> PriorityActionType.Less
            SwipeDirection.Right -> PriorityActionType.More
            SwipeDirection.Up -> PriorityActionType.Now
            SwipeDirection.Down -> PriorityActionType.Later
        }

        dispatchAppStoreAction(PriorityAction.TaskPriorityUpdateAction(item.id, reviewAction))
        sendEvent(ItemReview(item, reviewAction))

        if (isLast) {
            roundEndDelayJob = viewModelScope.launch {
                dispatchAppStoreAction(PriorityAction.UpdatePlannedTasksPriorityScore)
                delay(500)
                sendEvent(SetupRound(Final))
            }
        }
    }

    fun completeTask(item: ReviewItemUiModel) {
        item.isCompleted = true
        dispatchAppStoreAction(TaskAction.CompleteTaskAction(item.id, true))
        sendEvent(ReviewViewEvent.CompleteTask(item))
    }


}