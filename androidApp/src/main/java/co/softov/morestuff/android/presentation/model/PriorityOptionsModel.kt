package co.softov.morestuff.android.presentation.model

import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.model.PriorityOptionsResult

data class PriorityOptionsModel(
    val current: Priority,
    val options: List<PriorityOption>
)

fun PriorityOptionsResult.toModel() = PriorityOptionsModel(priority, options)