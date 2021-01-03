package co.softov.morestuff.androidApp.app.work

import android.app.AlarmManager
import android.content.Context
import android.os.SystemClock
import androidx.core.app.AlarmManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import co.softov.morestuff.androidApp.app.receiver.ScheduleReceiver
import co.softov.morestuff.androidApp.app.receiver.createReminderIntent
import co.softov.morestuff.androidApp.domain.redux.AppStore
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@KoinApiExtension
class ScheduleWorker(
    val context: Context,
    private val params: WorkerParameters
) : Worker(context, params), KoinComponent {

    private val store: AppStore by inject()

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun doWork(): Result {

        val scheduleId = params.inputData.getLong(PARAM_SCHEDULE_ID, -1)
        if (scheduleId > 0) {
            Timber.d("Starting service for schedule: $scheduleId")
            val alarmIntent = ScheduleReceiver.createReminderIntent(context, scheduleId)
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(1),
                alarmIntent
            )

        } else {
            Timber.e("Invalid schedule id")
        }

        return Result.success()
    }


    companion object {
        const val PARAM_SCHEDULE_ID = "scheduleId"

        fun createWorkerData(scheduleId: Long): Data =
            Data.Builder()
                .putLong(PARAM_SCHEDULE_ID, scheduleId)
                .build()
    }
}