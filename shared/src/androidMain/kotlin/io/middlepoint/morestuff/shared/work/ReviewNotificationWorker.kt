package io.middlepoint.morestuff.android.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.middleware.NotificationAction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        store.dispatchSuspend(NotificationAction.ShowReviewNotification)
        return Result.success()
    }
}
