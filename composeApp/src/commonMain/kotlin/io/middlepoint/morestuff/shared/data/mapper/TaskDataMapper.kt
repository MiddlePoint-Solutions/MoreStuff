@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.shared.data.mapper

import io.middlepoint.morestuff.shared.data.sync.TaskSync
import io.middlepoint.morestuff.shared.domain.enums.TaskType
import io.middlepoint.morestuff.shared.domain.model.core.Task

typealias TaskDb = io.middlepoint.morestuff.db.Task
typealias TaskDataMapper<R> = (
    id: Long,
    uuid: String,
    create_time: String,
    complete_time: String?,
    title: String,
    priority_score: Long,
    task_type: TaskType,
) -> R

fun makeTaskDataMapper(): TaskDataMapper<Task> = ::mapTaskDomain
fun makeTaskSyncMapper(): TaskDataMapper<TaskSync> = ::mapTaskSync // TODO: Continue

fun mapTaskDomain(
    id: Long,
    uuid: String,
    create_time: String,
    complete_time: String?,
    title: String,
    priority_score: Long,
    task_type: TaskType,
): Task = Task(
    id = id,
    uuid = uuid,
    createTime = create_time,
    completeTime = complete_time,
    title = title,
    priorityScore = priority_score,
    taskType = task_type
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