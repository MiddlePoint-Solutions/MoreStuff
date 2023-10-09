package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleReviewNotificationUseCase {
    operator fun invoke(hour: Int, minute: Int): Either<Failure, Boolean>
}

class ScheduleReviewNotificationUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleReviewNotificationUseCase {

    override fun invoke(hour: Int, minute: Int): Either<Failure, Boolean> {
        scheduler.scheduleNextReview(hour, minute)
        return Either.Right(true)
    }
}