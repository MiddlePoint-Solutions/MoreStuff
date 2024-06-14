package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.service.Scheduler

interface UpdateReviewNotificationScheduleUseCase {
    operator fun invoke(hour: Int, minute: Int): Either<Failure, Boolean>
}

class UpdateReviewNotificationScheduleUseCaseImpl(
    private val scheduler: Scheduler
) : UpdateReviewNotificationScheduleUseCase {

    override fun invoke(hour: Int, minute: Int): Either<Failure, Boolean> {
        scheduler.scheduleReviewWorker(hour, minute)
        return Either.Right(true)
    }
}