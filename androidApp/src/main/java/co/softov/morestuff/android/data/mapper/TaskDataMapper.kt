@file:Suppress("LocalVariableName")

package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.data.utils.let8
import co.softov.morestuff.android.domain.enums.TaskType
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.db.SelectAllComplete

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

typealias TaskWithScheduleDataMapper = (
    id: Long,
    uuid: String,
    create_time: String,
    complete_time: String?,
    title: String,
    priority_score: Long,
    task_type: TaskType,
    schedule_id: Long?,
    schedule_task_id: Long?,
    schedule_active: Boolean?,
    schedule_create_time: String?,
    schedule_timezone: String?,
    schedule_time_utc: String?,
    schedule_time_local: String?,
    schedule_type: ScheduleType?,
) -> TaskDomain

fun makeTaskWithScheduleDataMapper(): TaskWithScheduleDataMapper = ::mapTaskWithScheduleData
fun mapTaskWithScheduleData(
    id: Long,
    uuid: String,
    create_time: String,
    complete_time: String?,
    title: String,
    priority_score: Long,
    task_type: TaskType,
    schedule_id: Long?,
    schedule_task_id: Long?,
    schedule_active: Boolean?,
    schedule_create_time: String?,
    schedule_timezone: String?,
    schedule_time_utc: String?,
    schedule_time_local: String?,
    schedule_type: ScheduleType?,
): TaskDomain {
    return TaskDomain(
        id = id,
        uuid = uuid,
        createTime = create_time,
        completeTime = complete_time,
        title = title,
        priorityScore = priority_score,
        taskType = task_type,
        activeSchedule = let8(
            schedule_id,
            schedule_task_id,
            schedule_create_time,
            schedule_time_local,
            schedule_time_utc,
            schedule_timezone,
            schedule_active,
            schedule_type,
            block = ::ScheduleDomain
        )
    )
}

fun makeTaskDbMapper(): TaskDataMapper = ::mapTaskData

fun mapTaskData(
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

fun mapCompleteTaskData(input: SelectAllComplete): TaskDomain {
    return TaskDomain(
        id = input.id,
        uuid = input.uuid,
        createTime = input.create_time,
        completeTime = input.complete_time,
        title = input.title,
        priorityScore = input.priority_score,
        taskType = input.task_type
    )
}