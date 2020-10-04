package co.softov.morestuff.androidApp.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.softov.morestuff.androidApp.app.service.NotificationService

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_NOTIFICATION_REPLY = "co.softov.morestuff.androidApp.ACTION_NOTIFICATION_REPLY"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_NOTIFICATION_REPLY -> startNotificationService(context, intent)
        }
    }

    private fun startNotificationService(context: Context, intent: Intent) {
        Intent(context, NotificationService::class.java).let {
            it.putExtras(intent)
            context.startService(it)
        }
    }
}