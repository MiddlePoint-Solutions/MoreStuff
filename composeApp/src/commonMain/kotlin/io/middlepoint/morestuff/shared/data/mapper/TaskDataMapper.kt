@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.model.TaskData
import io.middlepoint.morestuff.shared.data.model.TaskSync
import io.middlepoint.morestuff.shared.domain.enums.TaskType
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

typealias DataMapper<I, O> = (I) -> O

fun makeTaskDataMapper1(): DataMapper<TaskData, Task> = ::mapTaskDomain1

fun mapTaskDomain1(
  taskData: TaskData
): Task = taskData.run {
  Task(
    id = id,
    createdAt = created_at.toString(),
    completedAt = completed_at?.toString(),
    updatedAt = updated_at.toString(),
    title = title,
    priorityScore = priority_score,
  )
}

fun makeTaskDataMapper(): TaskDataMapper<Task> = ::mapTaskDomain
fun makeTaskSyncMapper(): TaskDataMapper<TaskSync> = ::mapTaskSync // TODO: Continue

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
  createdAt = Instant.toString(),
  completedAt = completed_at?.toString(),
  updatedAt = updated_at.toString(),
  title = title,
  priorityScore = priority_score,
)

//private fun mapTaskSync(
//  id: Uuid,
//  created_at: Instant,
//  updated_at: Instant,
//  complete_time: String?,
//  title: String,
//  priority_score: Long,
//  task_type: TaskType,
//): TaskSync = TaskSync(
//  id = id,
//  createTime = complete_time,
//  completed_at = Instant?,
//completed_timezone: String?,
//title = title,
//priority = priority_score,
//taskType = task_type
//)