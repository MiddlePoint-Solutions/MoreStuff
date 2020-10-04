package co.softov.morestuff.androidApp.data.mapper

import co.softov.morestuff.androidApp.domain.model.Task

typealias taskDbMapper = (co.softov.morestuff.db.Task) -> Task

fun makeTaskDbMapper(): taskDbMapper = { task ->
    mapTaskDb(task)
}

fun mapTaskDb(input: co.softov.morestuff.db.Task): Task {
    return Task(
        id = input.id,
        createTime = input.create_time,
        completeTime = input.complete_time,
        title = input.title
    )
}