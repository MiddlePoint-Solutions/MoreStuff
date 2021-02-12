package co.softov.morestuff.android.presentation.list.schedule.model

data class ScheduleListItemViewModel(
    val scheduleId: Long,
    val taskId: Long,
    val scheduleTime: String,
    val taskTitle: String
) {
    var expanded: Boolean = false
}