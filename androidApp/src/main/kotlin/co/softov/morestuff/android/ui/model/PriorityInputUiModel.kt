package co.softov.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import co.softov.morestuff.android.domain.model.Priority
import kotlinx.datetime.LocalDateTime

@Immutable
sealed class PriorityUiModel {
    data object Now : PriorityUiModel()
    data object Later : PriorityUiModel()
    data class Plan(val localDateTime: LocalDateTime) : PriorityUiModel()
}
@Immutable
data class PriorityInputUiModel(
    val priority: PriorityUiModel,
    val planTime: ScheduleUiModel,
)

fun PriorityInputUiModel.mapToDomain() = when (priority) {
    PriorityUiModel.Later -> Priority.Later()
    PriorityUiModel.Now -> Priority.Now()
    is PriorityUiModel.Plan -> Priority.Plan(priority.localDateTime)
}