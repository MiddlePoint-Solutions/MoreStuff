package co.softov.morestuff.androidApp.app

import android.app.AlarmManager
import android.content.Context
import androidx.core.app.AlarmManagerCompat
import co.softov.morestuff.androidApp.app.receiver.ScheduleReceiver
import co.softov.morestuff.androidApp.app.receiver.cancelReminderIntent
import co.softov.morestuff.androidApp.app.receiver.createReminderIntent
import co.softov.morestuff.androidApp.data.utils.toEpochMilliseconds
import co.softov.morestuff.androidApp.domain.Scheduler
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.util.*

@KoinApiExtension
class SchedulerImpl(private val context: Context) : Scheduler, KoinComponent {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAtExact(scheduleId: Long, scheduleTime: String) {
        Timber.d("scheduleAtExact, schedule: $scheduleId = $scheduleTime (${scheduleTime.toEpochMilliseconds})")
        Timber.d("scheduleAtExact, current UTC time: ${Calendar.getInstance().timeInMillis}")
        val alarmIntent = ScheduleReceiver.createReminderIntent(context, scheduleId)
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            scheduleTime.toEpochMilliseconds,
            alarmIntent
        )
    }

    override fun cancelSchedule(scheduleId: Long) {
        ScheduleReceiver.cancelReminderIntent(context, scheduleId)
    }
}