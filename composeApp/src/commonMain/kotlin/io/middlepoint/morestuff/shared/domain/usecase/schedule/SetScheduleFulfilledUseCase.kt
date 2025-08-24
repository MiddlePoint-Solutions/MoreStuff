package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository

interface SetScheduleFulfilledUseCase {
    suspend operator fun invoke(scheduleId: Uuid): Either<Failure,Boolean>
}

class SetScheduleFulfilledUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : SetScheduleFulfilledUseCase {
    override suspend fun invoke(scheduleId: Uuid): Either<Failure, Boolean> {
        return scheduleRepository.setScheduleFulfilled(scheduleId)
    }
}