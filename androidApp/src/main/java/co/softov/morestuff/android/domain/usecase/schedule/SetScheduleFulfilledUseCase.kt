package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.repository.ScheduleRepository

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