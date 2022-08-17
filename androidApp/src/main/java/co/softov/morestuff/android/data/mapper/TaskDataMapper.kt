package co.softov.morestuff.android.data.mapper

import co.softov.morestuff.android.domain.model.Scope

typealias TaskData = co.softov.morestuff.db.Task

typealias taskDbMapper = (TaskData) -> Scope

fun makeTaskDbMapper(): taskDbMapper = { task ->
    mapTaskDb(task)
}

fun mapTaskDb(input: TaskData): Scope {
    return Scope(
        id = input.id,
        createTime = input.create_time,
        completeTime = input.complete_time,
        title = input.title
    )
}