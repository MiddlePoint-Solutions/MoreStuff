package co.softov.morestuff.android.domain.service

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: String)
    fun schedulePlannedPriorityUpdate()
    fun cancelSchedule(scheduleId: Long)
    fun cancelPlannedPriorityUpdate()
    fun scheduleNextReview(hour: Int, minute: Int, replaceExisting: Boolean)

}