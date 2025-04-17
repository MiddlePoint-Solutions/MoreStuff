package io.middlepoint.morestuff.shared.domain.model.core

import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class Task(
  val id: Uuid,
  val title: String,
  val createdAt: String,
  val updatedAt: String,
  val completedAt: String? = null,
  val priorityScore: Long,
  val schedule: List<Schedule> = listOf(),
  val extraDetails: Boolean = false,
) {
    val isComplete: Boolean get() = completedAt != null
    val hasSchedule: Boolean get() = schedule.any { it.isOneTime() }
    val hasReminder: Boolean get() = schedule.any { it.isReminder() }
    fun getScheduleOrNull() = schedule.firstOrNull { it.isOneTime() }
    fun getReminderOrNull() = schedule.firstOrNull { it.isReminder() }
}