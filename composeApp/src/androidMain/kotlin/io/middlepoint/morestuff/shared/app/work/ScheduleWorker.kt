package io.middlepoint.morestuff.shared.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.ScheduleAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScheduleWorker(
    context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        val scheduleId = params.inputData.getString(PARAM_SCHEDULE_ID)
        if (scheduleId != null) {
            Logger.d("Working on schedule: $scheduleId")
            store.dispatchSuspend(ScheduleAction.ExecuteScheduleAction(Uuid(scheduleId)))
        } else {
            Logger.e("Invalid schedule id")
        }

        return Result.success()
    }

    companion object {
        private const val PARAM_SCHEDULE_ID = "scheduleId"

        fun createWorkerData(scheduleId: Uuid): Data =
            Data.Builder()
                .putString(PARAM_SCHEDULE_ID, scheduleId.value)
                .build()
    }
}