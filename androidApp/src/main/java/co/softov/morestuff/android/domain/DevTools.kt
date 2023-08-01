package co.softov.morestuff.android.domain

interface DevTools {
    var showDebugMessages: Boolean
    var reminderDebugTime: Int
    fun testReviewNotification()
}