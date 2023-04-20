package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleReviewNotifications {
    suspend operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleReviewNotificationsImpl(
    private val scheduler: Scheduler
) : ScheduleReviewNotifications {

    override suspend fun invoke(): Either<Failure, Boolean> {
        scheduler.scheduleReviews()
        return Either.Right(true)
    }
}



