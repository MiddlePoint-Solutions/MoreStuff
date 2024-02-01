package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler
import co.softov.morestuff.android.domain.usecase.settings.GetAppSettingsUseCase

interface ScheduleWorkUseCase {
    suspend operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleWorkUseCaseImpl(
    private val scheduler: Scheduler,
) : ScheduleWorkUseCase {

    override suspend fun invoke(): Either<Failure, Boolean> {
        scheduler.schedulePlannedPriorityWorker()
        scheduler.scheduleReviewWorker()
        return Either.Right(true)
    }
}



