package co.softov.morestuff.android.ui.list.model

data class ScheduleListItemViewModel(
    val scheduleId: Long = 0,
    val taskId: Long = 0,
    val scheduleTime: String = "",
    val taskTitle: String = ""
)