package io.middlepoint.morestuff.shared.domain.model.core.migrate

import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleNew(
    val id: String = "",
    val taskId: String = "",
    val createTime: String = "",
    val scheduleLocalTime: String? = null,
    val scheduleUtcTime: String? = null,
    val timezone: String = "",
    val active: Boolean = false,
    val scheduleType: ScheduleType,
)

fun ScheduleNew.isReminder(): Boolean = scheduleType == ScheduleType.Reminder
fun ScheduleNew.isOneTime(): Boolean = scheduleType == ScheduleType.OneTime
