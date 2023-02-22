package co.softov.morestuff.android.ui.dashboard.options

import co.softov.morestuff.android.domain.model.PriorityOption

data class OptionListItemViewModel(
    val id: Long = 0,
    val time: Long = 0,
    val option: PriorityOption
)