package co.softov.morestuff.android.domain.model

data class ScheduleDomain(
    val id: Long,
    val taskId: Long,
    val createTime: String,
    val scheduleLocalTime: String?,
    val scheduleUtcTime: String?,
    val timezone: String,
    val active: Boolean,
    val scheduleType: ScheduleType,
)

fun ScheduleDomain.isReminder(): Boolean = scheduleType == ScheduleType.Reminder
fun ScheduleDomain.isOneTime(): Boolean = scheduleType == ScheduleType.OneTime
