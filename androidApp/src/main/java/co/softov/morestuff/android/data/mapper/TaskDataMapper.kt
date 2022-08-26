package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.Task

typealias TaskData = co.softov.morestuff.db.Task

typealias taskDbMapper = (TaskData) -> Task

fun makeTaskDbMapper(): taskDbMapper = { task ->
    mapTaskDb(task)
}

fun mapTaskDb(input: TaskData): Task {
    return Task(
        id = input.id,
        createTime = input.create_time,
        completeTime = input.complete_time,
        title = input.title
    )
}