package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase
import kotlinx.coroutines.runBlocking

interface ScheduleNotificationsAndWorkUseCase {
    operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleNotificationsAndWorkUseCaseImpl(
    private val scheduler: Scheduler,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase,
) : ScheduleNotificationsAndWorkUseCase {

    override fun invoke(): Either<Failure, Boolean> {
        val appSettings = runBlocking { getAppSettingsUseCase.invoke() }
        updateReviewNotificationScheduleUseCase.invoke(appSettings.reviewTime.first, appSettings.reviewTime.second, replaceExisting = false)
        scheduler.schedulePlannedPriorityUpdate()
        return Either.Right(true)
    }
}



