package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel

enum class ReviewRound {
    Priority, Final
}

data class ReviewModel(
    val round: ReviewRound = ReviewRound.Priority,
    val roundNumber: Int = 0,
    val roundItems: List<ScheduleListItemViewModel> = listOf(),
    val high: List<ScheduleListItemViewModel> = listOf(),
    val low: List<ScheduleListItemViewModel> = listOf(),
    val done: List<ScheduleListItemViewModel> = listOf(),
    val tomorrow: List<ScheduleListItemViewModel> = listOf(),
) : BaseViewState

sealed class ReviewViewEvent : BaseViewEvent {
    data class SetupInitialRound(
        val round: ReviewRound,
        val items: List<ScheduleListItemViewModel>,
    ) : ReviewViewEvent()

    data class SetupRound(val round: ReviewRound) : ReviewViewEvent()
    data class OnHighPriority(val item: ScheduleListItemViewModel) : ReviewViewEvent()
    data class OnLowPriority(val item: ScheduleListItemViewModel) : ReviewViewEvent()
    data class OnDone(val item: ScheduleListItemViewModel) : ReviewViewEvent()
    data class OnTomorrow(val item: ScheduleListItemViewModel) : ReviewViewEvent()
    data class Undo(val item: ScheduleListItemViewModel) : ReviewViewEvent()
}


