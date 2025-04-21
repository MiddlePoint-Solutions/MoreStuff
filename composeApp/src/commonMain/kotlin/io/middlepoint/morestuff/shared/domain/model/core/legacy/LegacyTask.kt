package io.middlepoint.morestuff.shared.domain.model.core.legacy

import io.middlepoint.morestuff.shared.domain.enums.TaskType
import kotlinx.serialization.Serializable

@Serializable
data class LegacyTask(
  val id: Long = 0L,
  val uuid: String = "",
  val title: String = "",
  val createTime: String = "",
  val completeTime: String? = null,
  val priorityScore: Long = 0,
  val taskType: TaskType = TaskType.System,
  val schedule: List<LegacySchedule> = listOf(),
  val extraDetails: Boolean = false,
  val deletedTime: String? = null,
) {

  val isComplete: Boolean get() = completeTime != null

}