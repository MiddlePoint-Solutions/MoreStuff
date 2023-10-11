package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase

interface ScheduleNotificationsAndWorkUseCase {
    suspend operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleNotificationsAndWorkUseCaseImpl(
    private val scheduler: Scheduler,
    private val getAppSettingsUseCase: GetAppSettingsUseCase,
    private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase,
) : ScheduleNotificationsAndWorkUseCase {

    override suspend fun invoke(): Either<Failure, Boolean> {
        val appSettings =  getAppSettingsUseCase()
        updateReviewNotificationScheduleUseCase.invoke(appSettings.reviewTime.first, appSettings.reviewTime.second, replaceExisting = false)
        scheduler.schedulePlannedPriorityUpdate()
        return Either.Right(true)
    }
}



