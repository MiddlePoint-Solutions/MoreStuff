package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewEvent
import co.softov.morestuff.android.app.presentation.viewmodel.BaseViewState
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel

enum class PriorityRound {
    Today, Now, Final
}

data class PriorityReviewModel(
    val round: PriorityRound = PriorityRound.Today,
    val roundNumber: Int = 0,
    val roundItems: List<ScheduleListItemViewModel> = listOf(),
    val now: List<ScheduleListItemViewModel> = listOf(),
    val snooze: List<ScheduleListItemViewModel> = listOf(),
    val done: List<ScheduleListItemViewModel> = listOf(),
    val later: List<ScheduleListItemViewModel> = listOf(),
    val today: List<ScheduleListItemViewModel> = listOf(),
    val tomorrow: List<ScheduleListItemViewModel> = listOf(),
) : BaseViewState

sealed class PriorityReviewViewEvent : BaseViewEvent {
    data class SetupInitialRound(
        val round: PriorityRound,
        val items: List<ScheduleListItemViewModel>,
    ) : PriorityReviewViewEvent()

    object SetupNextRound : PriorityReviewViewEvent()

    data class OnHighPriority(val item: ScheduleListItemViewModel) : PriorityReviewViewEvent()
    data class OnLowPriority(val item: ScheduleListItemViewModel) : PriorityReviewViewEvent()
    data class OnDone(val item: ScheduleListItemViewModel) : PriorityReviewViewEvent()
    data class OnLater(val item: ScheduleListItemViewModel) : PriorityReviewViewEvent()
    data class Undo(val item: ScheduleListItemViewModel) : PriorityReviewViewEvent()
}


