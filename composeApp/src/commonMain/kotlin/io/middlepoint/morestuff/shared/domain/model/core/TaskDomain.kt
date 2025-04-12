package io.middlepoint.morestuff.shared.domain.model.core

import io.middlepoint.morestuff.shared.domain.enums.TaskType
import kotlinx.serialization.Serializable

@Serializable
data class TaskDomain(
  val id: Long = 0L,
  val uuid: String = "",
  val title: String = "",
  val createTime: String = "",
  val completeTime: String? = null,
  val priorityScore: Long = 0,
  val taskType: TaskType = TaskType.System,
  val schedule: List<ScheduleDomain> = listOf(),
  val extraDetails: Boolean = false,
  val deletedTime: String? = null,
) {

    val isComplete: Boolean get() = completeTime != null
    val hasSchedule: Boolean get() = schedule.any { it.isOneTime() }
    val hasReminder: Boolean get() = schedule.any { it.isReminder() }
    fun getScheduleOrNull() = schedule.firstOrNull { it.isOneTime() }
    fun getReminderOrNull() = schedule.firstOrNull { it.isReminder() }
}