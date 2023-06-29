package co.softov.morestuff.android.ui.home

import co.softov.morestuff.android.domain.enums.RelativeDateDisplay
import kotlinx.datetime.LocalDateTime

enum class PriorityUI {
    Now, Later, Plan
}

data class PlanModel(
    val planTime: LocalDateTime,
    val relativeDisplay: RelativeDateDisplay = RelativeDateDisplay.Today,
    val hour: Int = 0,
    val minute: Int = 0,
    val epochMs: Long = 0,
)