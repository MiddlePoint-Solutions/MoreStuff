package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.middlepoint.morestuff.android.data.Constants
import io.middlepoint.morestuff.db.StuffDb

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(StuffDb.Schema, Constants.DATABASE_NAME)
    }
}
