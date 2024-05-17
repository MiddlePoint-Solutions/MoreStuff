package io.middlepoint.morestuff.shared.domain

import android.net.Uri

interface DevTools {

    var showDebugMessages: Boolean
    fun testReviewNotification()
    suspend fun exportData(uri: Uri)
    suspend fun importData(uri: Uri)
}