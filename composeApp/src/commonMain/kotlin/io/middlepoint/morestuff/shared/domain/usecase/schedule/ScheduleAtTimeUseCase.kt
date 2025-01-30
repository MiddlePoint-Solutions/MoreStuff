package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.service.Scheduler

interface ScheduleAtTimeUseCase {
    suspend operator fun invoke(scheduleId: Long, time: String, taskTitle: String): Either<Failure, Boolean>
}

class ScheduleAtTimeUseCaseImpl(
    private val scheduler: Scheduler
) : ScheduleAtTimeUseCase {

    override suspend fun invoke(scheduleId: Long, time: String, taskTitle: String): Either<Failure, Boolean> {
        scheduler.scheduleAtExact(scheduleId, time,taskTitle )
        return Either.Right(true)
    }
    
}



