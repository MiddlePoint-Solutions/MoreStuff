package io.middlepoint.morestuff.shared.data.model

import io.middlepoint.morestuff.shared.domain.enums.TaskType
import kotlinx.serialization.Serializable

@Serializable
data class TaskSync(
  val id: String,
  val createTime: String,
  val completeTime: String? = null,
  val title: String,
  val priority: Long,
  val taskType: TaskType,
)