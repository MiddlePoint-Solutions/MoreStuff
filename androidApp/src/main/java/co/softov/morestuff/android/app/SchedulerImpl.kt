package co.softov.morestuff.android.app

import android.content.Context
import androidx.work.*
import co.softov.morestuff.android.app.work.ScheduleWorker
import co.softov.morestuff.android.app.work.SmartReminderWorker
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.data.utils.toEpochMilliseconds
import co.softov.morestuff.android.domain.service.Scheduler
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class SchedulerImpl(context: Context) : Scheduler, KoinComponent {

    private val workManager = WorkManager.getInstance(context)

    override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
        val data = ScheduleWorker.createWorkerData(scheduleId)

        val delayTimeMillis =
            scheduleTime.toEpochMilliseconds - TimeUtils.nowUtcInstant.toEpochMilliseconds()

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
            1, TimeUnit.HOURS, 15, TimeUnit.MINUTES
        ).build().also { request ->
            workManager.enqueueUniquePeriodicWork(
                SMART_REMINDER_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                request
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
    }
}