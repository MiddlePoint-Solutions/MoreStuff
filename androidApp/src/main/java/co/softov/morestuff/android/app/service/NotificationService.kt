package co.softov.morestuff.android.app.service

import android.content.Intent
import co.softov.morestuff.android.app.receiver.getReplyIntentExtras
import co.softov.morestuff.android.domain.redux.middleware.ReminderAction.UserResponseAction
import timber.log.Timber

// TODO: this can be converted into a worker
class NotificationService : BaseService(NotificationService::class.java.simpleName) {

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            it.getReplyIntentExtras()?.also { reply ->
                Timber.d("onHandleIntent: $reply")
                dispatchStoreAction(UserResponseAction(reply.scheduleId, reply.type))
            }
        }
    }
}