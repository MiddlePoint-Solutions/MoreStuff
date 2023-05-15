package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.db.SelectAllComplete

typealias TaskData = co.softov.morestuff.db.Task

typealias TaskDataMapper = (TaskData) -> TaskDomain

fun makeTaskDbMapper(): TaskDataMapper = { task ->
    mapTaskData(task)
}

fun mapTaskData(input: TaskData): TaskDomain {
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