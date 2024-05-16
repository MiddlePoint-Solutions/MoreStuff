@file:Suppress("LocalVariableName")

package io.middlepoint.morestuff.android.data.mapper

import io.middlepoint.morestuff.android.domain.enums.TaskType
import io.middlepoint.morestuff.android.domain.model.TaskDomain

typealias TaskDb = io.middlepoint.morestuff.db.Task
typealias TaskDataMapper = (
    id: Long,
    uuid: String,
    create_time: String,
    complete_time: String?,
    title: String,
    priority_score: Long,
    task_type: TaskType,
) -> TaskDomain

fun makeTaskDbMapper(): TaskDataMapper = ::mapTaskDb

fun mapTaskDb(
    id: Long,
    uuid: String,
    create_time: String,
    complete_time: String?,
    title: String,
    priority_score: Long,
    task_type: TaskType,
): TaskDomain {
    return TaskDomain(
        id = id,
        uuid = uuid,
        createTime = create_time,
        completeTime = complete_time,
        title = title,
        priorityScore = priority_score,
        taskType = task_type
    )
}