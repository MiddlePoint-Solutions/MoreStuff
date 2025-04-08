package io.middlepoint.morestuff.shared.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        Logger.d("Work: ReviewNotificationWorker")
        store.dispatchSuspend(NotificationAction.ShowReviewNotification)
        Logger.d("Work: Done")
        return Result.success()
    }
}
