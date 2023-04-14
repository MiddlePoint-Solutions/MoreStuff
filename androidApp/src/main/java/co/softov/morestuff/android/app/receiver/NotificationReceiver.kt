package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.softov.morestuff.android.app.service.NotificationService
import co.softov.morestuff.android.domain.service.Notifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class NotificationReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        const val ACTION_NOTIFICATION_REPLY =
            "co.softov.morestuff.androidApp.ACTION_NOTIFICATION_REPLY"
        const val ACTION_NOTIFICATION_REVIEW =
            "co.softov.morestuff.androidApp.ACTION_NOTIFICATION_REVIEW"
    }

    private val notifier = get<Notifier>()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_NOTIFICATION_REPLY -> startNotificationService(context, intent)
            ACTION_NOTIFICATION_REVIEW -> showReviewActivity()
        }
    }

    private fun showReviewActivity() {
        notifier.showReviewNotification()
    }

    private fun startNotificationService(context: Context, intent: Intent) {
        Intent(context, NotificationService::class.java).let {
            it.putExtras(intent)
            context.startService(it)
        }
    }
}