package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.softov.morestuff.android.app.service.ScheduleService
import timber.log.Timber

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("ScheduleReceiver, onReceive ${intent.action}")
        when (intent.action) {
            ACTION_CREATE_REMINDER_SCHEDULE -> startCreateScheduleService(context, intent)
        }
    }

    private fun startCreateScheduleService(context: Context, intent: Intent) {
        Intent(context, ScheduleService::class.java).apply {
            putExtras(intent)
            context.startService(this)
        }
    }

    companion object {
        const val ACTION_CREATE_REMINDER_SCHEDULE =
            "co.softov.morestuff.androidApp.ACTION_CREATE_REMINDER_SCHEDULE"
    }
}