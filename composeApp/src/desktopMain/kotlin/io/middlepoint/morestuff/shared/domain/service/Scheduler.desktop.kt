package io.middlepoint.morestuff.shared.domain.service

actual class SchedulerImpl : Scheduler {
    override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
        TODO("Not yet implemented")
    }

    override fun schedulePlannedPriorityWorker() {
        // TODO:
    }

    override fun scheduleReviewWorker(hour: Int, minute: Int) {
        // TODO:
    }

    override fun cancelSchedule(scheduleId: Long) {
        TODO("Not yet implemented")
    }

    override fun cancelPlannedPriorityUpdate() {
        TODO("Not yet implemented")
    }

    override fun scheduleNextReview(hour: Int, minute: Int, replaceExisting: Boolean) {
        TODO("Not yet implemented")
    }
}