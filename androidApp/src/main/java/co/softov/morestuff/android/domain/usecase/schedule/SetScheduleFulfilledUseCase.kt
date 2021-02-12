package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface SetScheduleFulfilledUseCase {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Long>
}

class SetScheduleFulfilledUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : SetScheduleFulfilledUseCase {
    override suspend fun invoke(scheduleId: Long): SimpleResult<Long> {
        return scheduleRepository.setScheduleFulfilled(scheduleId)
    }
}