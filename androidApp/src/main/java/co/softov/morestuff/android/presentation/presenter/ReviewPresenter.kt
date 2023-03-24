package co.softov.morestuff.android.presentation.presenter

import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel

enum class PriorityRound {
    Initial, Next, Final
}

data class PriorityReviewModel(
    val round: PriorityRound = PriorityRound.Initial,
    val number: Int = 1,
    val items: List<ScheduleListItemViewModel> = listOf()
)


