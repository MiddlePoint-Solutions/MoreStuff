package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.Uuid

class SchedulerImpl : Scheduler {

    override fun scheduleAtExact(
      scheduleId: Uuid,
      scheduleTime: String,
      taskTitle: String,
      taskId: Uuid
    ) {
        TODO("Not yet implemented")
    }

    override fun scheduleDataSyncWorker() {
        // TODO:
    }

  /*  override fun scheduleReviewWorker(hour: Int, minute: Int) {
        // TODO:
    }*/

    override fun cancelSchedule(scheduleId: Long) {
        TODO("Not yet implemented")
    }

    override fun cancelPlannedPriorityUpdate() {
        TODO("Not yet implemented")
    }

    /*override fun scheduleNextReview(hour: Int, minute: Int, replaceExisting: Boolean) {
        TODO("Not yet implemented")
    }*/
}