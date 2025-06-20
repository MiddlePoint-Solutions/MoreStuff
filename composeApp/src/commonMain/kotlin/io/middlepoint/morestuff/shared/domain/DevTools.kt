package io.middlepoint.morestuff.shared.domain

import io.github.vinceglb.filekit.PlatformFile

interface DevTools {

    var showDebugMessages: Boolean
    var showDevSettings: Boolean
    fun testReviewNotification()
    suspend fun exportDatabase(uri: String)
    suspend fun importDatabase(uri: String)

    suspend fun exportJsonData()
    suspend fun importJsonData(jsonFile: PlatformFile): Boolean
    suspend fun importMigrationData(): Boolean
    fun removeMigrationData()

    suspend fun testDataPush()
    suspend fun testDataPull()
}