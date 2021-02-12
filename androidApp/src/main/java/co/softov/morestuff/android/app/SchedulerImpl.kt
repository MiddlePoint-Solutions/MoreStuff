package co.softov.morestuff.android.app

import android.content.Context
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.softov.morestuff.android.app.work.ScheduleWorker
import co.softov.morestuff.android.data.utils.TimeUtils
import co.softov.morestuff.android.data.utils.toEpochMilliseconds
import co.softov.morestuff.android.domain.service.Scheduler
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

@KoinApiExtension
class SchedulerImpl(context: Context) : Scheduler, KoinComponent {

    private val workManager = WorkManager.getInstance(context)

    private fun getScheduleWorkTag(scheduleId: Long) = "SCHEDULE_$scheduleId"

    override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
        val data = ScheduleWorker.createWorkerData(scheduleId)

        val delayTimeMillis =
            scheduleTime.toEpochMilliseconds - TimeUtils.currentUtcInstant.toEpochMilliseconds()

        val workConstraints =
            Constraints.Builder().apply {
                setTriggerContentMaxDelay(1, TimeUnit.SECONDS)
            }.build()

        val work = OneTimeWorkRequestBuilder<ScheduleWorker>()
            .setInitialDelay(delayTimeMillis, TimeUnit.MILLISECONDS)
            .setConstraints(workConstraints)
            .setInputData(data)
            .addTag(getScheduleWorkTag(scheduleId))
            .build()

        workManager.enqueue(work)
    }

    override fun cancelSchedule(scheduleId: Long) {
        workManager.cancelAllWorkByTag(getScheduleWorkTag(scheduleId))
    }

}