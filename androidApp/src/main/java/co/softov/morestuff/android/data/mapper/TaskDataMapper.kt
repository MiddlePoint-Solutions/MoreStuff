@file:Suppress("LocalVariableName")

package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.TaskDomain

typealias TaskDb = co.softov.morestuff.db.Task
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