package io.middlepoint.morestuff.shared.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.middlepoint.morestuff.android.data.Constants
import io.middlepoint.morestuff.db.StuffDb
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

actual class DriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(
            StuffDb.Schema,
            context,
            Constants.DATABASE_NAME,
            factory = RequerySQLiteOpenHelperFactory()
        )
}
