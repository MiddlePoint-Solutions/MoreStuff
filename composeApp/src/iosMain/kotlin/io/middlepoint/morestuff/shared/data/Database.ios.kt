package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.middlepoint.morestuff.android.data.Constants
import io.middlepoint.morestuff.db.StuffDb

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(StuffDb.Schema, Constants.DATABASE_NAME)
  }

  actual suspend fun provideDbDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>
  ): SqlDriver {
    return NativeSqliteDriver(StuffDb.Schema.synchronous(), Constants.DATABASE_NAME)
  }
}

