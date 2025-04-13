package io.middlepoint.morestuff.shared.domain.model.core.migrate

import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.core.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.model.core.isOneTime
import io.middlepoint.morestuff.shared.domain.model.core.isReminder
import kotlinx.serialization.Serializable

@Serializable
data class TaskNew(
  val id: String = "",
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