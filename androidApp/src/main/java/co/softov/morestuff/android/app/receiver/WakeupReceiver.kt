package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import co.softov.morestuff.android.app.service.BootService
import timber.log.Timber

class WakeupReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("WakeupReceiver") // This will not be allowed to execute in the background
    }

}