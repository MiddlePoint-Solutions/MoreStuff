package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.service.Scheduler

interface ScheduleWorkUseCase {
    suspend operator fun invoke(reviewTime: Pair<Int, Int>): Either<Failure, Boolean>
}

class ScheduleWorkUseCaseImpl(
    private val scheduler: Scheduler,
    private val updateReviewNotificationScheduleUseCase: UpdateReviewNotificationScheduleUseCase
) : ScheduleWorkUseCase {

    override suspend fun invoke(reviewTime: Pair<Int, Int>): Either<Failure, Boolean> {
        scheduler.schedulePlannedPriorityWorker()
        updateReviewNotificationScheduleUseCase(reviewTime.first, reviewTime.second)
        return Either.Right(true)
    }
}



