package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import co.softov.morestuff.android.app.service.BootService
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            startBootService(context)
        }
    }

    private fun startBootService(context: Context) {
        Intent(context, BootService::class.java).let {
            Timber.d("startBootService")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }
    }
}