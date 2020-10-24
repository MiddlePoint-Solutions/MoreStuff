package co.softov.morestuff.androidApp.app.service

import android.content.Intent
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.app.receiver.getScheduleIdExtra
import co.softov.morestuff.androidApp.domain.usecase.schedule.ExecuteSchedule
import org.koin.android.ext.android.inject
import timber.log.Timber

class ScheduleService : BaseService(ScheduleService::class.java.simpleName) {

    private val createReminderMessage: ExecuteSchedule by inject()

    override fun onHandleIntent(intent: Intent?) {
        Timber.d("onHandleIntent: $intent")
        serviceScope.launch {
            intent?.let {
                createReminderMessage(it.getScheduleIdExtra())
            }
        }
    }
}