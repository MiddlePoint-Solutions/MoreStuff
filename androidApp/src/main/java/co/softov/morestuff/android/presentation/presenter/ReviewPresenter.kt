package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.ui.list.model.TaskListItemViewModel

enum class ReviewRound {
    Priority, Final
}

data class ReviewModel(
    val round: ReviewRound = ReviewRound.Priority,
    val roundNumber: Int = 0,
    val roundItems: List<TaskListItemViewModel> = listOf(),
    val high: List<TaskListItemViewModel> = listOf(),
    val low: List<TaskListItemViewModel> = listOf(),
    val done: List<TaskListItemViewModel> = listOf(),
    val tomorrow: List<TaskListItemViewModel> = listOf(),
) : BaseViewState

sealed class ReviewViewEvent : BaseViewEvent {
    data class SetupInitialRound(
        val round: ReviewRound,
        val items: List<TaskListItemViewModel>,
    ) : ReviewViewEvent()

    data class SetupRound(val round: ReviewRound) : ReviewViewEvent()
    data class OnHighPriority(val item: TaskListItemViewModel) : ReviewViewEvent()
    data class OnLowPriority(val item: TaskListItemViewModel) : ReviewViewEvent()
    data class OnDone(val item: TaskListItemViewModel) : ReviewViewEvent()
    data class OnTomorrow(val item: TaskListItemViewModel) : ReviewViewEvent()
    data class Undo(val item: TaskListItemViewModel) : ReviewViewEvent()
}


