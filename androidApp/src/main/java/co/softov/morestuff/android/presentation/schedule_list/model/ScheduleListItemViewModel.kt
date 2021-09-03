package co.softov.morestuff.android.presentation.schedule_list.model

data class ScheduleListItemViewModel(
    val scheduleId: Long,
    val taskId: Long,
    val scheduleTime: String,
    val taskTitle: String
) {
    var expanded: Boolean = false
}