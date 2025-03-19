package io.middlepoint.morestuff.shared.data

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.middlepoint.morestuff.db.StuffDb
import java.io.File

/*actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        StuffDb.Schema.create(driver)
        return driver
    }
}*/





actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        // Crear el directorio de la base de datos si no existe
        val dbFolder = File(System.getProperty("user.home"), ".morestuff")
        if (!dbFolder.exists()) {
            dbFolder.mkdirs()
        }

        // Ruta del archivo de la base de datos
        val dbFile = File(dbFolder, "morestuff.db")
        val dbPath = dbFile.absolutePath

        // Crear o abrir la base de datos existente
        val driver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")

        // Verificar si la base de datos necesita ser creada o migrada
        if (!dbFile.exists()) {
            StuffDb.Schema.create(driver)
        }

        return driver
    }
}