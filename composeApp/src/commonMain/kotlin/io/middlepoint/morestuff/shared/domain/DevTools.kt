package io.middlepoint.morestuff.shared.domain

interface DevTools {

    var showDebugMessages: Boolean
    fun testReviewNotification()
    suspend fun exportData(uri: String)
    suspend fun importData(uri: String)
}