package co.softov.morestuff.android.presentation.model

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption

data class PriorityOptionsModel(
    val current: Priority,
    val options: List<PriorityOption>
)