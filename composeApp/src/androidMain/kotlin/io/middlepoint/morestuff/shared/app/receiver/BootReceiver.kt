package io.middlepoint.morestuff.shared.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.domain.usecase.schedule.BootCompleteSchedulerUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    val bootCompleteUseCase: BootCompleteSchedulerUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        Logger.d("onReceive")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            goAsync(Dispatchers.IO) {
                bootCompleteUseCase()
            }
        }
    }

}