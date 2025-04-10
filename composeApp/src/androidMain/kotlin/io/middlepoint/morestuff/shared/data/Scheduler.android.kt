package io.middlepoint.morestuff.shared.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.middlepoint.morestuff.shared.work.PlannedPriorityUpdateWorker
import io.middlepoint.morestuff.shared.app.work.ReviewNotificationWorker
import io.middlepoint.morestuff.shared.data.utils.inEpochMilliseconds
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.work.ScheduleWorker
import java.util.concurrent.TimeUnit

class SchedulerImpl(
    private val context: Context,
    private val timeManager: TimeManager,
    private val workManager: WorkManager
) : Scheduler {

    override fun scheduleAtExact(
        scheduleId: Long,
        scheduleTime: String,
        taskTitle: String,
        taskId: Long,
    ) {
        val data = ScheduleWorker.createWorkerData(scheduleId)

        val delayTimeMillis =
            scheduleTime.inEpochMilliseconds - timeManager.nowUtcInstant.toEpochMilliseconds()

        val workConstraints = Constraints.Builder().apply {
            setTriggerContentMaxDelay(1, TimeUnit.MINUTES)
        }.build()

        val work = OneTimeWorkRequestBuilder<ScheduleWorker>().apply {
            setInitialDelay(delayTimeMillis, TimeUnit.MILLISECONDS)
            setConstraints(workConstraints)
            setInputData(data)
            addTag(getScheduleWorkTag(scheduleId))
        }.build()

        workManager.enqueue(work)
    }

    override fun schedulePlannedPriorityWorker() {
        PeriodicWorkRequestBuilder<PlannedPriorityUpdateWorker>(
            repeatInterval = 20,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).build().also { request ->
            workManager.enqueueUniquePeriodicWork(
                PLANNED_PRIORITY_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

/*    override fun scheduleReviewWorker(hour: Int, minute: Int) {

        val currentTime = timeManager.nowLocalDateTime

        val scheduleTime = when {
            currentTime.hour > hour -> timeManager.tomorrowLocalDateTimeString(hour, minute)
            currentTime.hour < hour -> timeManager.todayLocalDateTimeString(hour, minute)
            currentTime.minute > minute -> timeManager.tomorrowLocalDateTimeString(hour, minute)
            else -> timeManager.todayLocalDateTimeString(hour, minute)
        }

        val delayTimeMillis =
            scheduleTime.inEpochMilliseconds - timeManager.nowUtcInstant.toEpochMilliseconds()

        val workConstraints = Constraints.Builder().apply {
            setTriggerContentMaxDelay(1, TimeUnit.MINUTES)
        }.build()

        val work = OneTimeWorkRequestBuilder<ReviewNotificationWorker>().apply {
            setInitialDelay(delayTimeMillis, TimeUnit.MILLISECONDS)
            setConstraints(workConstraints)
            addTag(PRIORITY_REVIEW_WORK)
        }.build()

        workManager.enqueueUniqueWork(PRIORITY_REVIEW_WORK, ExistingWorkPolicy.REPLACE, work)
    }*/

    override fun cancelSchedule(scheduleId: Long) {
        workManager.cancelAllWorkByTag(getScheduleWorkTag(scheduleId))
    }

    override fun cancelPlannedPriorityUpdate() {
        workManager.cancelAllWorkByTag(PLANNED_PRIORITY_WORK)
    }

    private fun getScheduleWorkTag(scheduleId: Long) = "SCHEDULE_$scheduleId"

    companion object {
        private const val PLANNED_PRIORITY_WORK = "SmartReminder"
        private const val PRIORITY_REVIEW_WORK = "PriorityReview"
    }
}