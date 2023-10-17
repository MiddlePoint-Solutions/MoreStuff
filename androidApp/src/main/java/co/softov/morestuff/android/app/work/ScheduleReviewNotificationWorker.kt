package co.softov.morestuff.android.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.softov.morestuff.android.domain.redux.AppStore
import co.softov.morestuff.android.domain.redux.middleware.PriorityAction
import co.softov.morestuff.android.domain.usecase.schedule.UpdateReviewNotificationScheduleUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingUseCase
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import timber.log.Timber

class ScheduleReviewNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val getSettingsUseCase: GetAppSettingsUseCase by inject()
    private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase by inject()

    override suspend fun doWork(): Result {
        Timber.d("Work: ScheduleReviewNotificationWorker")
        val settings = getSettingsUseCase()
        updateReviewNotificationScheduleUseCase(settings.reviewTime.first, settings.reviewTime.second, replaceExisting = false)
        Timber.d("Work: Done")
        return Result.success()
    }
}