package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Worker
import kotlin.coroutines.CoroutineContext


actual class DriverFactory : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

  private fun createWebWorkerDriver() : SqlDriver {
    return WebWorkerDriver(jsWorker())
  }

  actual fun provideDbDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>
  ): SqlDriver {
    return createWebWorkerDriver().also {
      launch { schema.create(it).await() }
    }
  }
}

internal fun jsWorker(): Worker = js("""new Worker(new URL("sqljs.worker.js", import.meta.url))""")


