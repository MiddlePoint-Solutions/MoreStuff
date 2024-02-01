package co.softov.morestuff.android.data.service

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import co.softov.morestuff.android.domain.service.DataMigrationHelper
import co.softov.morestuff.db.StuffDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class DataMigrationHelperImpl(
    private val context: Context,
) : DataMigrationHelper {

    override suspend fun exportDatabase(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentDBPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
            val currentDB = File(currentDBPath)
            if (currentDB.exists()) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val src = FileInputStream(currentDB)
                    src.copyTo(outputStream)
                    src.close()
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }

    override suspend fun importDatabase(uri: Uri): Boolean  = withContext(Dispatchers.IO) {
        Timber.d("importDatabase: $uri")
        try {
            val currentDBPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
            context.deleteDatabase(DATABASE_NAME)
            val currentDB = File(currentDBPath)
            currentDB.createNewFile()
            Timber.d("importDatabase: $currentDB")
            if (currentDB.exists()) {
                Timber.d("importDatabase: exists")
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    Timber.d("importDatabase: inputStream ready")
                    val src = FileOutputStream(currentDB)
                    inputStream.copyTo(src)
                    src.close()
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error attempting to import database")
            e.printStackTrace()
        }
        return@withContext false
    }

    companion object {
        private const val DATABASE_NAME = "morestuff.db"

    }

}