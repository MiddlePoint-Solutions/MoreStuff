package co.softov.morestuff.android.ui.list.model

data class ScheduleListItemViewModel(
    val scheduleId: Long,
    val taskId: Long,
    val scheduleTime: String,
    val taskTitle: String
)