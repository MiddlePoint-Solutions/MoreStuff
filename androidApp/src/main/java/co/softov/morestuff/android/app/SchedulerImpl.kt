package co.softov.morestuff.android.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.work.*
import co.softov.morestuff.android.app.receiver.NotificationReceiver
import co.softov.morestuff.android.app.receiver.createCancelReviewPendingIntent
import co.softov.morestuff.android.app.receiver.createReviewIntent
import co.softov.morestuff.android.app.receiver.createReviewPendingIntent
import co.softov.morestuff.android.app.receiver.getReviewIntentRequestCode
import co.softov.morestuff.android.app.work.ScheduleWorker
import co.softov.morestuff.android.app.work.SmartReminderWorker
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.data.utils.inEpochMilliseconds
import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.domain.service.Scheduler
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SchedulerImpl(
    private val context: Context,
    private val timeManager: TimeManager
) : Scheduler, KoinComponent {

    private val workManager = WorkManager.getInstance(context)

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

    override fun scheduleSmartReminder() {
        PeriodicWorkRequestBuilder<SmartReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).build().also { request ->
            workManager.enqueueUniquePeriodicWork(
                SMART_REMINDER_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    override fun scheduleReviews() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val morningIntent =
            NotificationReceiver.createReviewIntent(context, ReviewNotification.Morning)

        var pendingIntent = NotificationReceiver.createCancelReviewPendingIntent(
            context,
            morningIntent,
            ReviewNotification.Morning
        )
        if (pendingIntent == null) {
            Timber.d("Morning review notification does not exist - creating...")
            Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 50)
            }.also {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    it.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    NotificationReceiver.createReviewPendingIntent(
                        context,
                        morningIntent,
                        ReviewNotification.Morning
                    )
                )
            }
        } else {
            Timber.d("Morning Review exists")
        }

        val eveningIntent =
            NotificationReceiver.createReviewIntent(context, ReviewNotification.Evening)

        pendingIntent = NotificationReceiver.createCancelReviewPendingIntent(
            context,
            eveningIntent,
            ReviewNotification.Evening
        )

        if (pendingIntent == null) {
            Timber.d("Evening review notification does not exist - creating...")
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 20)
                set(Calendar.MINUTE, 0)
            }

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                NotificationReceiver.createReviewPendingIntent(
                    context,
                    eveningIntent,
                    ReviewNotification.Evening
                )
            )
        } else {
            Timber.d("Evening Review already exists")
        }

    }

    override fun cancelSchedule(scheduleId: Long) {
        workManager.cancelAllWorkByTag(getScheduleWorkTag(scheduleId))
    }

    override fun cancelSmartReminder() {
        workManager.cancelAllWorkByTag(SMART_REMINDER_WORK)
    }

    private fun getScheduleWorkTag(scheduleId: Long) = "SCHEDULE_$scheduleId"

    companion object {
        private const val SMART_REMINDER_WORK = "SmartReminder"
        private const val PRIORITY_REVIEW_WORK = "PriorityReview"
    }
}