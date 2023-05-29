package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.service.Scheduler

interface ScheduleAtTimeUseCase {
    suspend operator fun invoke(scheduleId: Long, time: String): Either<Failure, Boolean>
}

class ScheduleAtTimeUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleAtTimeUseCase {

    override suspend fun invoke(scheduleId: Long, time: String): Either<Failure, Boolean> {
        scheduler.scheduleAtExact(scheduleId, time)
        return Either.Right(true)
    }
    
}



