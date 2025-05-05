package io.middlepoint.morestuff.shared.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.redux.AppStore
import io.middlepoint.morestuff.shared.domain.redux.action.NotificationAction
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class NotificationReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_NOTIFICATION_REPLY -> onNotificationReply(intent)
            ACTION_NOTIFICATION_REVIEW -> onReviewNotification()
        }
    }

    private fun onReviewNotification() {
        goAsync(Dispatchers.Default) {
            val store: AppStore = get()
            store.dispatchSuspend(NotificationAction.ShowReviewNotification)
        }
    }

    private fun onNotificationReply(intent: Intent) {
        intent.getReplyIntentExtras()?.let {
            goAsync(Dispatchers.Default) {
                val store: AppStore = get()
                store.dispatchSuspend(
                    NotificationAction.UserResponseAction(
                        Uuid(it.scheduleId),
                        it.type
                    )
                )
            }
        } ?: Logger.w("!!! Notification Reply missing extras !!!")
    }

    companion object {
        const val ACTION_NOTIFICATION_REPLY =
            "io.middlepoint.morestuff.androidApp.ACTION_NOTIFICATION_REPLY"
        const val ACTION_NOTIFICATION_REVIEW =
            "io.middlepoint.morestuff.androidApp.ACTION_NOTIFICATION_REVIEW"
        const val ACTION_NOTIFICATION_REMINDER =
            "io.middlepoint.morestuff.androidApp.ACTION_NOTIFICATION_REMINDER"

        const val KEY_SCHEDULE_ID = "SCHEDULE_ID"
        const val KEY_REPLY_EXTRA = "REPLY_EXTRA"
        const val KEY_REVIEW_EXTRA = "REVIEW_EXTRA"
        const val KEY_TASK_ID = "TASK_ID"

        const val REQUEST_CODE_REVIEW = 4242
    }
}