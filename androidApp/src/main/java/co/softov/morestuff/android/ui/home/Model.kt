package co.softov.morestuff.android.ui.home

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.data.utils.currentTimeZoneInstant
import co.softov.morestuff.android.domain.model.Priority
import kotlinx.datetime.LocalDateTime

@Immutable
sealed class PriorityModel {
    object Now : PriorityModel()
    object Later : PriorityModel()
    data class Plan(val localDateTime: LocalDateTime) : PriorityModel()
}

@Immutable
data class PlanModel(
    val localDateTime: LocalDateTime,
    val displayDate: String,
    val displayTime: String,
    val hour: Int = localDateTime.hour,
    val minute: Int = localDateTime.minute,
    val epochMs: Long = localDateTime.currentTimeZoneInstant.toEpochMilliseconds(),
)

fun PriorityInputModel.mapToDomain() = when (priority) {
    PriorityModel.Later -> Priority.Later()
    PriorityModel.Now -> Priority.Now()
    is PriorityModel.Plan -> Priority.Plan(priority.localDateTime.toString())
}

@Immutable
data class PriorityInputModel(
    val priority: PriorityModel,
    val planTime: PlanModel,
)

