package io.middlepoint.morestuff.android.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.android.domain.model.Priority
import kotlinx.datetime.LocalDateTime

@Immutable
sealed class PriorityUiModel {
    data object Now : PriorityUiModel()
    data object Later : PriorityUiModel()
    data class Plan(val localDateTime: LocalDateTime) : PriorityUiModel()
}
@Immutable
data class PriorityInputUiModel(
    val priority: PriorityUiModel = PriorityUiModel.Now,
    val planTime: ScheduleUiModel, // TODO: this should probably be nullable or at least deconstruct members
)

fun PriorityUiModel.mapToDomain() = when (this) {
    PriorityUiModel.Later -> Priority.Later()
    PriorityUiModel.Now -> Priority.Now()
    is PriorityUiModel.Plan -> Priority.Plan(localDateTime)
}