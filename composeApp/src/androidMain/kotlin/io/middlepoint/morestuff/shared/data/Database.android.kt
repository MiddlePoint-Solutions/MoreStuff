package io.middlepoint.morestuff.shared.data

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.middlepoint.morestuff.android.data.Constants
import io.middlepoint.morestuff.db.StuffDb
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

actual class DriverFactory(
  private val context: Context
) {

  actual fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
    return AndroidSqliteDriver(
      schema.synchronous(),
      context,
      Constants.DATABASE_NAME,
      factory = RequerySQLiteOpenHelperFactory()
    )
  }
}
