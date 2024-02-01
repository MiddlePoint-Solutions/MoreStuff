package co.softov.morestuff.android.ui.review

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewModel
import co.softov.morestuff.android.domain.enums.PriorityActionType
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.redux.middleware.TaskAction
import co.softov.morestuff.android.domain.redux.state.SettingAction
import co.softov.morestuff.android.domain.usecase.scope.GetScopesUseCase
import co.softov.morestuff.android.domain.usecase.task.GetReviewTasksUseCase
import co.softov.morestuff.android.ui.review.ReviewRound.Final
import co.softov.morestuff.android.ui.review.ReviewRound.Review
import co.softov.morestuff.android.ui.review.ReviewViewEvent.ItemReview
import co.softov.morestuff.android.ui.review.ReviewViewEvent.FinalRound
import co.softov.morestuff.android.ui.review.ReviewViewEvent.Undo
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.model.map.ReviewTasksMapper
import co.softov.morestuff.android.ui.review.ReviewViewEvent.SetupReviewRound
import co.softov.morestuff.android.ui.review.swipeable.SwipeDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val getReviewTasksUseCase: GetReviewTasksUseCase,
    private val getScopesUseCase: GetScopesUseCase,
    private val reviewTasksMapper: ReviewTasksMapper,
) : BaseViewModel<ReviewModel, ReviewViewEvent>(ReviewModel()) {

    private var roundEndDelayJob: Job? = null

    var scopes by mutableStateOf(listOf<ScopeDomain>())
        private set

    var reviewHintEnabled by mutableStateOf(false)
        private set

    fun load(scopeId: Long) {
        loadData()
        viewModelScope.launch {
            getScopesUseCase().onRight {
                scopes = it
                setCurrentScope(scopeId)
            }
        }
    }

    fun setCurrentScope(scopeId: Long) {
        if (state.currentScope.id == scopeId && state.items.isNotEmpty()) {
            return
        }

        viewModelScope.launch {
            getReviewTasksUseCase(scopeId).map {
                val scope = scopes.first { scope -> scope.id == scopeId }
                val tasks = reviewTasksMapper.map(it).shuffled()
                sendEvent(SetupReviewRound(scope, tasks))
            }
        }
    }

    override fun onReduceState(event: ReviewViewEvent) = when (event) {

        is SetupReviewRound -> ReviewModel(
            round = Review(event.currentScope.id),
            currentScope = event.currentScope,
            items = event.items
        )

        is FinalRound -> state.copy(round = Final)

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
                    PriorityAction.UndoTaskPriorityUpdateAction(item.id, item.priorityScore)
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

        dispatchAppStoreAction(PriorityAction.TaskPriorityUpdateAction(item.id, reviewAction))
        sendEvent(ItemReview(item, reviewAction))

        if (state.items.first() == item) {
            roundEndDelayJob = viewModelScope.launch {
                dispatchAppStoreAction(PriorityAction.UpdatePlannedPriorityAction)
                delay(500)
                sendEvent(FinalRound)
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
                sendEvent(FinalRound)
            }
        }
    }

    fun toggleHintArrowPriority() {
        dispatchAppStoreAction(SettingAction.EnableReviewHint(enable = !reviewHintEnabled))
    }

}