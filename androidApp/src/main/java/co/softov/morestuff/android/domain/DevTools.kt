package co.softov.morestuff.android.domain

interface DevTools {
    var debugReminders: Boolean
    var todayDebugTime: Int
    fun getDebugMessageSwitchState(): Boolean

    fun testReviewNotification()
}