package co.softov.morestuff.android.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.softov.morestuff.android.domain.usecase.schedule.BootCompleteSchedulerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class BootReceiver : BroadcastReceiver(), KoinComponent {

    val bootCompleteUseCase: BootCompleteSchedulerUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            goAsync(Dispatchers.IO) {
                bootCompleteUseCase()
            }
        }
    }

}