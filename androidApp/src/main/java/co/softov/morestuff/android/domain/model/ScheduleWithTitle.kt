package co.softov.morestuff.android.domain.model

data class ScheduleWithTitle(
    val scheduleId: Long,
    val taskId: Long,
    val scheduleTime: String?,
    val taskTitle: String
)