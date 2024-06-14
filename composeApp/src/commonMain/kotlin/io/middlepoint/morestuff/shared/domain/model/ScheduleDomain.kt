package io.middlepoint.morestuff.shared.domain.model

import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDomain(
    val id: Long = 0L,
    val taskId: Long = 0L,
    val createTime: String = "",
    val scheduleLocalTime: String? = null,
    val scheduleUtcTime: String? = null,
    val timezone: String = "",
    val active: Boolean = false,
    val scheduleType: ScheduleType,
)

fun ScheduleDomain.isReminder(): Boolean = scheduleType == ScheduleType.Reminder
fun ScheduleDomain.isOneTime(): Boolean = scheduleType == ScheduleType.OneTime
