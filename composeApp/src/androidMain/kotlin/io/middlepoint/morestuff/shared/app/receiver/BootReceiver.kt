package io.middlepoint.morestuff.shared.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent

class BootReceiver : BroadcastReceiver(), KoinComponent {

    // TODO: check if this is useful to use when device has booted?

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d("onReceive")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            goAsync(Dispatchers.IO) {
            }
        }
    }

}