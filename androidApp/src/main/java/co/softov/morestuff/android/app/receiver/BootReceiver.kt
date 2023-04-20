package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            startBootService(context)
        }
    }

    private fun startBootService(context: Context) {
        // TODO: BootService was causing crashes, should use work manager instead.
        /*Intent(context, BootService::class.java).let {
            Timber.d("startBootService")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it)
            } else {
                context.startService(it)
            }
        }*/
    }
}