package io.middlepoint.morestuff.shared

import android.content.Context
import android.net.Uri
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

actual class DataMigrationHelperImpl(
    private val context: Context,
    private val logger: Logger,
) : DataMigrationHelper {

    override suspend fun exportDatabase(uri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentDBPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
            val currentDB = File(currentDBPath)
            if (currentDB.exists()) {
                context.contentResolver.openOutputStream(Uri.parse(uri))?.use { outputStream ->
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

    override suspend fun importDatabase(uri: String): Boolean  = withContext(Dispatchers.IO) {
        Logger.d("importDatabase: $uri")
        try {
            val currentDBPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
            context.deleteDatabase(DATABASE_NAME)
            val currentDB = File(currentDBPath)
            currentDB.createNewFile()
            logger.d("importDatabase: $currentDB")
            if (currentDB.exists()) {
                Logger.d("importDatabase: exists")
                context.contentResolver.openInputStream(Uri.parse(uri))?.use { inputStream ->
                    logger.d("importDatabase: inputStream ready")
                    val src = FileOutputStream(currentDB)
                    inputStream.copyTo(src)
                    src.close()
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            logger.e( "Error attempting to import database", e)
            e.printStackTrace()
        }
        return@withContext false
    }

    companion object {
        private const val DATABASE_NAME = "morestuff.db"

    }

}