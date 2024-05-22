package io.middlepoint.morestuff.android.app.work

import android.app.AlarmManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.ScheduleAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ScheduleWorker(
    context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        val scheduleId = params.inputData.getLong(PARAM_SCHEDULE_ID, -1)
        if (scheduleId > 0) {
            Timber.d("Working on schedule: $scheduleId")
            store.dispatchSuspend(ScheduleAction.ExecuteScheduleAction(scheduleId))
        } else {
            Timber.e("Invalid schedule id")
        }

        return Result.success()
    }

    companion object {
        private const val PARAM_SCHEDULE_ID = "scheduleId"

        fun createWorkerData(scheduleId: Long): Data =
            Data.Builder()
                .putLong(PARAM_SCHEDULE_ID, scheduleId)
                .build()
    }
}