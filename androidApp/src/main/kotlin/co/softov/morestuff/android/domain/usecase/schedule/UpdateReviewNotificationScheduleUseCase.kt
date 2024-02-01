package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface UpdateReviewNotificationScheduleUseCase {
    operator fun invoke(hour: Int, minute: Int, replaceExisting: Boolean): Either<Failure, Boolean>
}

class UpdateReviewNotificationScheduleUseCaseImpl(
    private val scheduler: Scheduler
) : UpdateReviewNotificationScheduleUseCase {

    override fun invoke(hour: Int, minute: Int, replaceExisting: Boolean): Either<Failure, Boolean> {
        scheduler.scheduleNextReview(hour, minute, replaceExisting)
        return Either.Right(true)
    }
}