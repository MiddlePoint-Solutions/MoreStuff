package io.middlepoint.morestuff.shared.domain.model.core

import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class Schedule(
    val id: Uuid,
    val taskId: Uuid,
    val createdAt: String = "",
    val updatedAt: String = "",
    val scheduledAt: String = "",
    val timezone: String = "",
    val active: Boolean = false,
    val scheduleType: ScheduleType,
)

fun Schedule.isReminder(): Boolean = scheduleType == ScheduleType.Reminder
fun Schedule.isOneTime(): Boolean = scheduleType == ScheduleType.OneTime
