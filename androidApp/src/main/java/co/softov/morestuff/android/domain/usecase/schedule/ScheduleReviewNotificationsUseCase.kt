package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleReviewNotificationsUseCase {
    suspend operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleReviewNotificationsUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleReviewNotificationsUseCase {

    override suspend fun invoke(): Either<Failure, Boolean> {
        scheduler.scheduleReviews()
        return Either.Right(true)
    }
}



