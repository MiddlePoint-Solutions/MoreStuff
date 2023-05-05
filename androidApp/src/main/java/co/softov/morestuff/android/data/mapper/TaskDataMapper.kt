package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.TaskDomain

typealias TaskData = co.softov.morestuff.db.Task

typealias taskDbMapper = (TaskData) -> TaskDomain

fun makeTaskDbMapper(): taskDbMapper = { task ->
    mapTaskDb(task)
}

fun mapTaskDb(input: TaskData): TaskDomain {
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