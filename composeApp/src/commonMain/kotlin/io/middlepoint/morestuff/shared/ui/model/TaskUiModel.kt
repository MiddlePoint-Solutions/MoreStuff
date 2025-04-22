package io.middlepoint.morestuff.shared.ui.model

import androidx.compose.runtime.Immutable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class TaskUiModel(
    val id: Uuid = Uuid(""),
    val title: String = "",
    val createTime: String = "",
    val completeTime: String = "",
    val isComplete: Boolean = false,
    val priorityScore: Long = 0L,
    val position: Int = 0,
    val extraDetails: Boolean = false,
    val hasSchedule: Boolean = false,
    val hasReminder: Boolean = false,
    val scheduleTime: String = ""
)
