package co.softov.morestuff.android.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction
import co.softov.morestuff.android.domain.service.Notifier
import co.softov.morestuff.android.domain.usecase.schedule.UpdateReviewNotificationScheduleUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun doWork(): Result {
        Timber.d("Work: ReviewNotificationWorker")
        store.dispatchSuspend(NotificationAction.ShowReviewNotification)
        Timber.d("Work: Done")
        return Result.success()
    }
}
