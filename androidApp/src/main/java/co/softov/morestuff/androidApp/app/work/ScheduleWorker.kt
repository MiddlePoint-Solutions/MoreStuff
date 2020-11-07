package co.softov.morestuff.androidApp.app.work

import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import co.softov.morestuff.androidApp.app.receiver.setScheduleIdExtra
import co.softov.morestuff.androidApp.app.service.ScheduleService
import co.softov.morestuff.androidApp.domain.redux.AppStore
import co.softov.morestuff.androidApp.domain.redux.middleware.ScheduleAction
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.experimental.property.inject
import timber.log.Timber

@KoinApiExtension
class ScheduleWorker(
    val context: Context,
    private val params: WorkerParameters
) : Worker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override fun doWork(): Result {

        val scheduleId = params.inputData.getLong(PARAM_SCHEDULE_ID, -1)
        if (scheduleId > 0) {
            Timber.d("Starting service for schedule: $scheduleId")
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