@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.model.TaskSync
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlinx.datetime.Instant

typealias TaskDataMapper<R> = (
  id: String,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
) -> R

fun makeTaskDataMapper(): TaskDataMapper<Task> = ::mapTaskDomain
fun makeTaskSyncMapper(): TaskDataMapper<TaskSync> = ::mapTaskSync // TODO: Continue

fun mapTaskDomain(
  id: String,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
): Task = Task(
  id = id,
  createdAt = Instant.toString(),
  completedAt = completed_at?.toString(),
  updatedAt = updated_at.toString(),
  title = title,
  priorityScore = priority_score,
)

private fun mapTaskSync(
  id: Long,
  uuid: String,
  create_time: String,
  complete_time: String?,
  title: String,
  priority_score: Long,
  task_type: TaskType,
): TaskSync = TaskSync(
  id = uuid,
  createTime = create_time,
  completeTime = complete_time,
  title = title,
  priority = priority_score,
  taskType = task_type
)