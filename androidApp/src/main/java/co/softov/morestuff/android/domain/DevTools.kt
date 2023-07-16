package co.softov.morestuff.android.domain

interface DevTools {
    var debugReminders: Boolean
    var todayDebugTime: Int
    var keepScreenOn: Boolean
    fun getDebugMessageSwitchState(): Boolean

    fun testReviewNotification()
}