package co.softov.morestuff.android.app

import android.app.AlarmManager
import android.content.Context
import androidx.work.*
import co.softov.morestuff.android.app.receiver.NotificationReceiver
import co.softov.morestuff.android.app.receiver.createCancelReviewPendingIntent
import co.softov.morestuff.android.app.receiver.createReviewIntent
import co.softov.morestuff.android.app.receiver.createReviewPendingIntent
import co.softov.morestuff.android.app.work.ScheduleWorker
import co.softov.morestuff.android.app.work.PlannedPriorityUpdateWorker
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.data.utils.inEpochMilliseconds
import co.softov.morestuff.android.domain.service.Scheduler
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

    override fun schedulePlannedPriorityUpdate() {
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

    override fun scheduleNextReview() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reviewIntent = NotificationReceiver.createReviewIntent(context)
        val pendingIntent = NotificationReceiver.createCancelReviewPendingIntent(
            context,
            reviewIntent
        )

        if (pendingIntent == null) {
            Timber.d("Review notification does not exist - creating...")
            Calendar.getInstance().apply {
                val hourOfDay = get(Calendar.HOUR_OF_DAY)
                if (hourOfDay in 10..19) {
                    set(Calendar.HOUR_OF_DAY, 20)
                    set(Calendar.MINUTE, 0)
                } else {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                }
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
        } else {
            Timber.d("Review exists")
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