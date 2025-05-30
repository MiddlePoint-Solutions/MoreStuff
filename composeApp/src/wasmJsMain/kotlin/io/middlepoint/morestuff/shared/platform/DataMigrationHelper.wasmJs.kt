package io.middlepoint.morestuff.shared.platform

class DataMigrationHelperImpl : DataMigrationHelper {
    override suspend fun exportDatabase(uri: String): Boolean {
        return true
//        TODO("Not yet implemented")
    }

    override suspend fun importDatabase(uri: String): Boolean {
        return true
//        TODO("Not yet implemented")
    }
}