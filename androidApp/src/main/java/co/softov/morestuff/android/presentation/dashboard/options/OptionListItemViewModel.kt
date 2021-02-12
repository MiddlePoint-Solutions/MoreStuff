package co.softov.morestuff.android.presentation.dashboard.options

import co.softov.morestuff.android.domain.enums.PriorityOption

data class OptionListItemViewModel(
    val id: Long = 0,
    val time: Long = 0,
    val option: PriorityOption
)