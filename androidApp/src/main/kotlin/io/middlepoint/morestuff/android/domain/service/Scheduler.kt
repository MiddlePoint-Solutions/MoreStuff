package io.middlepoint.morestuff.android.domain.service

interface Scheduler {

    fun scheduleAtExact(scheduleId: Long, scheduleTime: String)
    fun schedulePlannedPriorityWorker()
    fun scheduleReviewWorker(hour: Int, minute: Int)
    fun cancelSchedule(scheduleId: Long)
    fun cancelPlannedPriorityUpdate()
    fun scheduleNextReview(hour: Int, minute: Int, replaceExisting: Boolean)

}