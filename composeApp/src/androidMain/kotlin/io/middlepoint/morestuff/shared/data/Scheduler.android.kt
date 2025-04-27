package io.middlepoint.morestuff.shared.data

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.middlepoint.morestuff.shared.work.DataSyncWorker
import io.middlepoint.morestuff.shared.data.utils.inEpochMilliseconds
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import io.middlepoint.morestuff.shared.domain.service.TimeManager
import io.middlepoint.morestuff.shared.work.ScheduleWorker
import java.util.concurrent.TimeUnit

class SchedulerImpl(
    private val timeManager: TimeManager,
    private val workManager: WorkManager
) : Scheduler {

    override fun scheduleAtExact(
        scheduleId: Uuid,
        scheduleTime: String,
        taskTitle: String,
        taskId: Uuid,
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

    override fun scheduleDataSyncWorker() {
        val networkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        PeriodicWorkRequestBuilder<DataSyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 60,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).apply {
            setConstraints(networkConstraints)
        }.build().also { request ->
            workManager.enqueueUniquePeriodicWork(
                DATA_SYNC_WORK,
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

    override fun cancelSchedule(scheduleId: Uuid) {
        workManager.cancelAllWorkByTag(getScheduleWorkTag(scheduleId))
    }

    override fun cancelPlannedPriorityUpdate() {
        workManager.cancelAllWorkByTag(DATA_SYNC_WORK)
    }

    private fun getScheduleWorkTag(scheduleId: Uuid) = "SCHEDULE_${scheduleId.value}"

    companion object {
        private const val DATA_SYNC_WORK = "SmartReminder"
        private const val PRIORITY_REVIEW_WORK = "PriorityReview"
    }
}