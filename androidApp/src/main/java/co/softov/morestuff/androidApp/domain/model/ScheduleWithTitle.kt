package co.softov.morestuff.androidApp.domain.model

data class ScheduleWithTitle(
    val scheduleId: Long,
    val taskId: Long,
    val scheduleTime: Long,
    val taskTitle: String
)