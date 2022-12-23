package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.state.ReviewAction.SetSchedulesForReview
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.store.InitAction

data class ReviewState(
    val schedules: List<ScheduleWithTitle> = listOf()
)

sealed class ReviewAction : Action.FeatureAction() {

    object InitReview : ReviewAction()
    data class SetSchedulesForReview(val schedules: List<ScheduleWithTitle>) : ReviewAction()
}

fun AppState.reduceReviewState(action: Action): AppState {
    return when (action) {
        is InitAction,
        is ReviewAction -> copy(reviewState = reviewState.reduce(action))
        else -> this
    }
}

fun ReviewState.reduce(action: Action): ReviewState {
    return when (action) {
        is SetSchedulesForReview -> copy(schedules = action.schedules)
        else -> this
    }
}

