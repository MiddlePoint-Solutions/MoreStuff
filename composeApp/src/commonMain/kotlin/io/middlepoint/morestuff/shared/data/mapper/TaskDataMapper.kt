@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlinx.datetime.Instant

typealias TaskDataMapper<R> = (
  id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
) -> R

fun makeTaskDataMapper(): TaskDataMapper<Task> = ::mapTaskDomain

fun mapTaskDomain(
  id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
): Task = Task(
  id = id,
  createdAt = created_at.toString(),
  completedAt = completed_at?.toString(),
  updatedAt = updated_at.toString(),
  timezone = completed_timezone,
  title = title,
  priorityScore = priority_score,
)
