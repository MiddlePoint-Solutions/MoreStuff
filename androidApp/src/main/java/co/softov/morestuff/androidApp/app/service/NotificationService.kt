package co.softov.morestuff.androidApp.app.service

import android.content.Intent
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.app.receiver.getReplyIntentExtras
import co.softov.morestuff.androidApp.domain.usecase.schedule.HandleScheduleResponse
import org.koin.android.ext.android.inject
import timber.log.Timber

class NotificationService : BaseService(NotificationService::class.java.simpleName) {

    private val handleNotificationResponse: HandleScheduleResponse by inject()

    override fun onHandleIntent(intent: Intent?) {
        serviceScope.launch {
            intent?.let {
                it.getReplyIntentExtras()?.also { reply ->
                    Timber.d("onHandleIntent: $reply")
                    handleNotificationResponse(
                        reply.scheduleId,
                        reply.type
                    )
                }
            }
        }
    }
}