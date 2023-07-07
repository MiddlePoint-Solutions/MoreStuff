package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import co.softov.morestuff.android.app.work.NotificationResponseWorker
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.NotificationAction
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber

class NotificationReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_NOTIFICATION_REPLY -> onNotificationReply(context, intent)
            ACTION_NOTIFICATION_REVIEW -> onReviewNotification()
        }
    }

    private fun onReviewNotification() {
        goAsync(Dispatchers.Default) {
            val store: AppStore = get()
            store.dispatchSuspend(NotificationAction.ShowReviewNotification)
        }
    }

    private fun onNotificationReply(context: Context, intent: Intent) {
        intent.getReplyIntentExtras()?.let {
            val work = OneTimeWorkRequestBuilder<NotificationResponseWorker>().apply {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(NotificationResponseWorker.createWorkerData(it.scheduleId, it.type))
                addTag(NotificationResponseWorker.createWorkerTag(it.scheduleId))
            }.build()
            WorkManager.getInstance(context).enqueue(work)
        } ?: Timber.w("!!! Notification Reply missing extras !!!")
    }

    companion object {
        const val ACTION_NOTIFICATION_REPLY =
            "co.softov.morestuff.androidApp.ACTION_NOTIFICATION_REPLY"
        const val ACTION_NOTIFICATION_REVIEW =
            "co.softov.morestuff.androidApp.ACTION_NOTIFICATION_REVIEW"
        const val ACTION_NOTIFICATION_REMINDER =
            "co.softov.morestuff.androidApp.ACTION_NOTIFICATION_REVIEW"

        const val KEY_SCHEDULE_ID = "SCHEDULE_ID"
        const val KEY_REPLY_EXTRA = "REPLY_EXTRA"
        const val KEY_REVIEW_EXTRA = "REVIEW_EXTRA"
        const val KEY_TASK_ID = "TASK_ID"

        const val REQUEST_CODE_REVIEW = 4242
    }
}