package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.middlepoint.morestuff.db.Message
import io.middlepoint.morestuff.db.Schedule
import io.middlepoint.morestuff.db.Scope
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.db.Task

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory) = StuffDb(
    driver = driverFactory.createDriver(),
    taskAdapter = Task.Adapter(
        task_typeAdapter = EnumColumnAdapter()
    ),
    scheduleAdapter = Schedule.Adapter(
        schedule_typeAdapter = EnumColumnAdapter()
    ),
    messageAdapter = Message.Adapter(
        content_typeAdapter = IntColumnAdapter,
        reply_typeAdapter = IntColumnAdapter
    ),
    scopeAdapter = Scope.Adapter(
        scope_orderAdapter = IntColumnAdapter,
    )
)