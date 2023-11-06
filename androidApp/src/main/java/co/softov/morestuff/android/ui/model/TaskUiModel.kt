package co.softov.morestuff.android.ui.model

data class TaskUiModel(
    val id: Long,
    val title: String,
    val createTime: String,
    val completeTime: String,
    val isComplete: Boolean,
    val priorityScore: Long,
    val position: Int,
    val extraDetails: Boolean,
    val hasSchedule: Boolean,
    val hasReminder: Boolean,
    val isSelected: Boolean,
)
