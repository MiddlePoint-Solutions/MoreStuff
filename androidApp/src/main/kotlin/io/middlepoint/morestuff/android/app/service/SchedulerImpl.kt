package io.middlepoint.morestuff.android.app.service

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import androidx.work.*
import io.middlepoint.morestuff.android.app.receiver.NotificationReceiver
import io.middlepoint.morestuff.android.app.receiver.createCancelReviewPendingIntent
import io.middlepoint.morestuff.android.app.receiver.createReviewIntent
import io.middlepoint.morestuff.android.app.receiver.createReviewPendingIntent
import io.middlepoint.morestuff.android.app.work.ScheduleWorker
import io.middlepoint.morestuff.android.app.work.PlannedPriorityUpdateWorker
import io.middlepoint.morestuff.android.app.work.ReviewNotificationWorker
import io.middlepoint.morestuff.android.domain.service.TimeManager
import io.middlepoint.morestuff.shared.data.utils.inEpochMilliseconds
import io.middlepoint.morestuff.android.domain.service.Scheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SchedulerImpl(
    private val context: Context,
    private val timeManager: TimeManager,
) : Scheduler, KoinComponent {

    private val workManager: WorkManager by inject()

    override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
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

    override fun scheduleReviewWorker(hour: Int, minute: Int) {

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
    }

    override fun scheduleNextReview(hour: Int, minute: Int, replaceExisting: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reviewIntent = NotificationReceiver.createReviewIntent(context)
        val pendingIntent = NotificationReceiver.createCancelReviewPendingIntent(
            context,
            reviewIntent
        )
        when {
            pendingIntent == null -> {
                Timber.d("Review notification does not exist - creating...")
                setReviewAlarm(alarmManager, hour, minute, reviewIntent)
            }

            replaceExisting -> {
                alarmManager.cancel(pendingIntent)
                setReviewAlarm(alarmManager, hour, minute, reviewIntent)
            }
        }
    }

    private fun setReviewAlarm(
        alarmManager: AlarmManager,
        hour: Int,
        minute: Int,
        reviewIntent: Intent,
    ) {
        Calendar.getInstance().apply {
            val hourOfDay = get(Calendar.HOUR_OF_DAY)
            if (hourOfDay > hour) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.also {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                it.timeInMillis,
                NotificationReceiver.createReviewPendingIntent(
                    context,
                    reviewIntent,
                )
            )
        }
    }

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