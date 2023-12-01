package co.softov.morestuff.android.ui.review

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.state.TaskAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.usecase.task.GetReviewTasksUseCase
import co.softov.morestuff.android.ui.review.ReviewRound.Final
import co.softov.morestuff.android.ui.review.ReviewRound.Review
import co.softov.morestuff.android.ui.review.ReviewViewEvent.ItemReview
import co.softov.morestuff.android.ui.review.ReviewViewEvent.SetupInitialRound
import co.softov.morestuff.android.ui.review.ReviewViewEvent.SetupRound
import co.softov.morestuff.android.ui.review.ReviewViewEvent.Undo
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.model.map.ReviewTasksMapper
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val scopeId: Long,
    private val getReviewTasksUseCase: GetReviewTasksUseCase,
    private val reviewTasksMapper: ReviewTasksMapper,
) : BaseViewModel<ReviewModel, ReviewViewEvent>(ReviewModel()) {

    private var roundEndDelayJob: Job? = null

    var reviewHintEnabled by mutableStateOf(false)
        private set

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
    }

    override fun onAppStateChange(state: AppState) {
        reviewHintEnabled = state.settings.enableReviewHint
    }

    private fun setInitialState(round: ReviewRound = Review) {
        viewModelScope.launch {
            getReviewTasksUseCase(scopeId).map {
                val tasks = reviewTasksMapper.map(it).shuffled()
                sendEvent(SetupInitialRound(round, tasks))
            }
        }
    }

    fun undo(item: ReviewItemUiModel) {
        roundEndDelayJob?.cancel()
        state.actions.firstOrNull {
            it.first.id == item.id
        }?.let {
            when (it.second) {
                PriorityActionType.Done -> dispatchAppStoreAction(
                    TaskAction.CompleteTasksAction(listOf(item.id), false)
                )

                else -> dispatchAppStoreAction(
                    PriorityAction.UndoTaskPriorityUpdateAction(item.id, item.priorityScore, scopeId)
                )
            }
        }
        sendEvent(Undo(item))
    }

    fun onTaskSwiped(
        item: ReviewItemUiModel,
        direction: SwipeDirection,
    ) {
        val reviewAction = when (direction) {
            SwipeDirection.Left -> PriorityActionType.Less
            SwipeDirection.Right -> PriorityActionType.More
            SwipeDirection.Up -> PriorityActionType.Now
            SwipeDirection.Down -> PriorityActionType.Later
            SwipeDirection.None -> return
        }

        dispatchAppStoreAction(PriorityAction.TaskPriorityUpdateAction(item.id, reviewAction, scopeId))
        sendEvent(ItemReview(item, reviewAction))

        if (state.items.first() == item) {
            roundEndDelayJob = viewModelScope.launch {
                dispatchAppStoreAction(PriorityAction.UpdatePlannedPriorityAction(scopeId))
                delay(500)
                sendEvent(SetupRound(Final))
            }
        }
    }

    fun completeTask(item: ReviewItemUiModel) {
        sendEvent(ItemReview(item, PriorityActionType.Done))
        dispatchAppStoreAction(TaskAction.CompleteTasksAction(listOf(item.id), true))
        goToFinalRound(item)
    }

    private fun goToFinalRound(item: ReviewItemUiModel) {
        if (state.items.first() == item) {
            roundEndDelayJob = viewModelScope.launch {
                delay(500)
                sendEvent(SetupRound(Final))
            }
        }
    }

    fun toggleHintArrowPriority() {
        dispatchAppStoreAction(SettingAction.EnableReviewHint(enable = !reviewHintEnabled))
    }

}