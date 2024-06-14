package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository

interface SetScheduleFulfilledUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure,Long>
}

class SetScheduleFulfilledUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : SetScheduleFulfilledUseCase {
    override suspend fun invoke(scheduleId: Long): Either<Failure,Long> {
        return scheduleRepository.setScheduleFulfilled(scheduleId)
    }
}