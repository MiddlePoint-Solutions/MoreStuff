package co.softov.morestuff.android.app.work

import android.app.AlarmManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ScheduleWorker(
    val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun doWork(): Result {
        val scheduleId = params.inputData.getLong(PARAM_SCHEDULE_ID, -1)
        if (scheduleId > 0) {
            Timber.d("Working on schedule: $scheduleId")
            store.dispatch(ScheduleAction.ExecuteScheduleAction(scheduleId))
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