package co.softov.morestuff.android.domain.model

data class PriorityOptionsResult(
    val priority: Priority,
    val options: List<PriorityOption>,
)