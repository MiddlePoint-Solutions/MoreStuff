package co.softov.morestuff.android.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PriorityReviewWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        store.dispatchSuspend(ReminderAction.SmartReminderAction)
        return Result.success()
    }
}