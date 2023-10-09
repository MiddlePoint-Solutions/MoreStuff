package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleNotificationsAndWorkUseCase {
    operator fun invoke(): Either<Failure, Boolean>
}

class ScheduleNotificationsAndWorkUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleNotificationsAndWorkUseCase {

    override fun invoke(): Either<Failure, Boolean> {
        //scheduler.scheduleNextReview()
        scheduler.schedulePlannedPriorityUpdate()
        return Either.Right(true)
    }
}



