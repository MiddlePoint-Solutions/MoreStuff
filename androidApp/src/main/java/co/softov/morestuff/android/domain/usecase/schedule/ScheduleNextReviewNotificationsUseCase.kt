package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleNextReviewNotificationsUseCase {
    operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleNextReviewNotificationsUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleNextReviewNotificationsUseCase {

    override fun invoke(): Either<Failure, Boolean> {
        scheduler.scheduleNextReview()
        return Either.Right(true)
    }
}



