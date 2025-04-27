package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.Uuid

interface Scheduler {
    fun scheduleAtExact(scheduleId: Uuid, scheduleTime: String, taskTitle: String, taskId: Uuid)
    fun scheduleDataSyncWorker()
    fun cancelSchedule(scheduleId: Uuid)
    fun cancelPlannedPriorityUpdate()
}