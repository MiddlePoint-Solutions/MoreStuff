package co.softov.morestuff.android.domain.service

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: String)
    fun scheduleSmartReminderWork()
    fun cancelSchedule(scheduleId: Long)

}