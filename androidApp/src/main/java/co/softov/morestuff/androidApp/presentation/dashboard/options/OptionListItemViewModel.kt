package co.softov.morestuff.androidApp.presentation.dashboard.options

import co.softov.morestuff.androidApp.domain.enums.PriorityOption

data class OptionListItemViewModel(
    val id: Long = 0,
    val time: Long = 0,
    val option: PriorityOption
)