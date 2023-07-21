package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.enums.RelativeDateDisplay
import co.softov.morestuff.android.domain.model.Priority
import kotlinx.datetime.LocalDateTime

@Immutable
sealed class PriorityModel {
    object Now : PriorityModel()
    object Later : PriorityModel()
    data class Plan(
        val planTime: LocalDateTime,
        val relativeDisplay: RelativeDateDisplay = RelativeDateDisplay.Today,
        val hour: Int = 0,
        val minute: Int = 0,
        val epochMs: Long = 0,
    ) : PriorityModel()
}

fun PriorityModel.mapToDomain() = when (this) {
    PriorityModel.Later -> Priority.Later()
    PriorityModel.Now -> Priority.Now()
    is PriorityModel.Plan -> Priority.Plan(planTime.toString())
}

