package co.softov.morestuff.androidApp.app.service

import android.content.Intent
import co.softov.morestuff.androidApp.app.receiver.getReplyIntentExtras
import co.softov.morestuff.androidApp.domain.redux.middleware.ResponseAction.ScheduleResponseAction
import timber.log.Timber

class NotificationService : BaseService(NotificationService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            it.getReplyIntentExtras()?.also { reply ->
                Timber.d("onHandleIntent: $reply")
                dispatchStoreAction(ScheduleResponseAction(reply.scheduleId, reply.type))
            }
        }
    }
}