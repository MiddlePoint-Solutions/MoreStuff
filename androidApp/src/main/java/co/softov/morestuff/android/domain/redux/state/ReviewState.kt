package co.softov.morestuff.android.domain.redux.state

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.store.Action

data class ReviewState(
    val schedules: List<ScheduleWithTitle> = listOf()
)

sealed class ReviewAction : Action.FeatureAction() {
    data class TaskReviewResults(
        val tomorrow: List<Long>,
        val high: List<Long>,
        val low: List<Long>,
        val done: List<Long>,
    ) : ReviewAction()
}

fun AppState.reduceReviewState(action: Action): AppState {
    return when (action) {
        is ReviewAction -> copy(reviewState = reviewState.reduce(action))
        else -> this
    }
}

fun ReviewState.reduce(action: Action): ReviewState {
    return when (action) {
        else -> this
    }
}

