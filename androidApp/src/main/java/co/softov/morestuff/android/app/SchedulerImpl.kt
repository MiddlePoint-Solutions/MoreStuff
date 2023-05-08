package co.softov.morestuff.android.app

import android.app.AlarmManager
import android.content.Context
import androidx.work.*
import co.softov.morestuff.android.app.receiver.NotificationReceiver
import co.softov.morestuff.android.app.receiver.createReviewIntent
import co.softov.morestuff.android.app.work.ScheduleWorker
import co.softov.morestuff.android.app.work.SmartReminderWorker
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.data.utils.inEpochMilliseconds
import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.domain.service.Scheduler
import org.koin.core.component.KoinComponent
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

        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 50)
        }.also {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                it.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                NotificationReceiver.createReviewIntent(context, ReviewNotification.Morning)
            )
        }

        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 12)
        }.also {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                it.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                NotificationReceiver.createReviewIntent(context, ReviewNotification.Afternoon)
            )
        }

        Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 30)
        }.also {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                it.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                NotificationReceiver.createReviewIntent(context, ReviewNotification.Evening)
            )
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