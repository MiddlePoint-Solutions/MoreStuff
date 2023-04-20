package co.softov.morestuff.android.domain.service

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: String)
    fun scheduleSmartReminder()
    fun cancelSchedule(scheduleId: Long)
    fun cancelSmartReminder()
    fun scheduleReviews()

}