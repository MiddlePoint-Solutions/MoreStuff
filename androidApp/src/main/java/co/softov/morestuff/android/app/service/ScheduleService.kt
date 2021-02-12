package co.softov.morestuff.android.app.service

import android.content.Intent
import co.softov.morestuff.android.app.receiver.getScheduleIdExtra
import co.softov.morestuff.android.domain.redux.middleware.ScheduleAction.ExecuteScheduleAction
import timber.log.Timber

class ScheduleService : BaseService(ScheduleService::class.java.simpleName) {

    override fun onHandleIntent(intent: Intent?) {
        Timber.d("onHandleIntent: $intent")
        intent?.let {
            val scheduleId = it.getScheduleIdExtra()
            dispatchStoreAction(ExecuteScheduleAction(scheduleId))
        }
    }
}