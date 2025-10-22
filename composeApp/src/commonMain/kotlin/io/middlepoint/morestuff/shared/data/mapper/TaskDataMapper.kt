@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.Sync
import io.middlepoint.morestuff.shared.data.sync.TaskSync
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Task
import kotlin.time.Instant

typealias TaskDataMapper<R> = (
  id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
  deleted: Boolean
) -> R

fun makeTaskDataMapper(): TaskDataMapper<Task> = ::mapTaskData
fun makeTaskSyncMapper(): TaskDataMapper<Sync<TaskSync>> = ::mapTaskSync

fun mapTaskData(
  id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
  deleted: Boolean
) = Task(
  id = id,
  createdAt = created_at.toString(),
  completedAt = completed_at?.toString(),
  updatedAt = updated_at.toString(),
  timezone = completed_timezone,
  title = title,
  priorityScore = priority_score,
  deleted = deleted
)

fun mapTaskSync(
  id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  completed_at: Instant?,
  completed_timezone: String?,
  title: String,
  priority_score: Long,
  deleted: Boolean
) = Sync(
  id = id,
  createdAt = created_at,
  updatedAt = updated_at,
  deleted = deleted,
  data = TaskSync(
    id,
    created_at,
    updated_at,
    completed_at,
    completed_timezone,
    title,
    priority_score,
    deleted
  )
)


