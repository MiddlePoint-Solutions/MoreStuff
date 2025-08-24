package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.middlepoint.morestuff.db.Activities
import io.middlepoint.morestuff.db.Messages
import io.middlepoint.morestuff.db.Messages_extra
import io.middlepoint.morestuff.db.Schedules
import io.middlepoint.morestuff.db.Scopes
import io.middlepoint.morestuff.db.StuffDb
import io.middlepoint.morestuff.db.Tasks
import io.middlepoint.morestuff.db.Tasks_scopes
import io.middlepoint.morestuff.db.Url_metadata
import io.middlepoint.morestuff.shared.data.adapter.InstantColumnAdapter
import io.middlepoint.morestuff.shared.data.adapter.UuidColumnAdapter

expect class DriverFactory {
  fun provideDbDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>
  ): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory) = StuffDb(
  driver = driverFactory.provideDbDriver(StuffDb.Schema),
  activitiesAdapter = Activities.Adapter(
    idAdapter = UuidColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
  ),
  tasksAdapter = Tasks.Adapter(
    idAdapter = UuidColumnAdapter,
    updated_atAdapter = InstantColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
    completed_atAdapter = InstantColumnAdapter,
  ),
  schedulesAdapter = Schedules.Adapter(
    idAdapter = UuidColumnAdapter,
    task_idAdapter = UuidColumnAdapter,
    scheduled_atAdapter = InstantColumnAdapter,
    updated_atAdapter = InstantColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
    schedule_typeAdapter = EnumColumnAdapter()
  ),
  messagesAdapter = Messages.Adapter(
    idAdapter = UuidColumnAdapter,
    task_idAdapter = UuidColumnAdapter,
    schedule_idAdapter = UuidColumnAdapter,
    content_typeAdapter = IntColumnAdapter,
    updated_atAdapter = InstantColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
  ),
  tasks_scopesAdapter = Tasks_scopes.Adapter(
    task_idAdapter = UuidColumnAdapter,
    scope_idAdapter = UuidColumnAdapter,
    updated_atAdapter = InstantColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
  ),
  messages_extraAdapter = Messages_extra.Adapter(
    idAdapter = UuidColumnAdapter,
    message_idAdapter = UuidColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
    updated_atAdapter = InstantColumnAdapter
  ),
  url_metadataAdapter = Url_metadata.Adapter(
    message_idAdapter = UuidColumnAdapter,
  ),
  scopesAdapter = Scopes.Adapter(
    idAdapter = UuidColumnAdapter,
    scope_orderAdapter = IntColumnAdapter,
    updated_atAdapter = InstantColumnAdapter,
    created_atAdapter = InstantColumnAdapter,
  ),
)