package io.middlepoint.morestuff.shared.platform

interface DataMigrationHelper {

    suspend fun exportDatabase(uri: String) : Boolean
    suspend fun importDatabase(uri: String) : Boolean

}