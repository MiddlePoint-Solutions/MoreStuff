package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.middlepoint.morestuff.db.StuffDb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

actual class DriverFactory : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

  actual fun provideDbDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>
  ): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
      .also { launch { schema.create(it).await() } }
  }
}
