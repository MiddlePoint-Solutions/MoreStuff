package io.middlepoint.morestuff.shared

interface DataMigrationHelper {

    suspend fun exportDatabase(uri: String) : Boolean

    suspend fun importDatabase(uri: String) : Boolean

}