@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.ScheduleSync
import io.middlepoint.morestuff.shared.data.sync.Sync
import io.middlepoint.morestuff.shared.data.sync.TaskRelationSync
import io.middlepoint.morestuff.shared.data.sync.TaskSync
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import kotlin.time.Instant

typealias ScheduleDataMapper<R> = (
  id: Uuid,
  task_id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  scheduled_at: Instant,
  timezone: String,
  active: Boolean,
  schedule_type: ScheduleType,
  deleted: Boolean,
) -> R

fun makeScheduleDataMapper(): ScheduleDataMapper<Schedule> = ::mapScheduleData
fun makeScheduleSyncMapper(): ScheduleDataMapper<TaskRelationSync<ScheduleSync>> = ::mapScheduleSync

fun mapScheduleData(
  id: Uuid,
  task_id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  scheduled_at: Instant,
  timezone: String,
  active: Boolean,
  schedule_type: ScheduleType,
  deleted: Boolean,
): Schedule = Schedule(
  id = id,
  taskId = task_id,
  createdAt = created_at.toString(),
  updatedAt = updated_at.toString(),
  scheduledAt = scheduled_at.toString(),
  timezone = timezone,
  active = active,
  scheduleType = schedule_type,
  deleted = deleted
)

fun mapScheduleSync(
  id: Uuid,
  task_id: Uuid,
  created_at: Instant,
  updated_at: Instant,
  scheduled_at: Instant,
  timezone: String,
  active: Boolean,
  schedule_type: ScheduleType,
  deleted: Boolean,
) = TaskRelationSync(
  id = id,
  taskId = task_id,
  createdAt = created_at,
  updatedAt = updated_at,
  deleted = deleted,
  data = ScheduleSync(
    id,
    task_id,
    created_at,
    updated_at,
    scheduled_at,
    timezone,
    active,
    schedule_type,
    deleted
  )
)