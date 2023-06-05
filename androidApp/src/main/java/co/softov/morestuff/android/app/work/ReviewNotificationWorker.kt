package co.softov.morestuff.android.app.work

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction
import co.softov.morestuff.android.domain.service.Notifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val store: AppStore by inject()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())
        return Result.success()
    }

    private fun createNotification(): Notification =
        NotificationCompat.Builder(applicationContext, Notifier.REVIEW_CHANNEL_ID)
            .setSmallIcon(R.drawable.priority_48px)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER).build()

    companion object {

        private const val NOTIFICATION_ID = 91192

    }
}