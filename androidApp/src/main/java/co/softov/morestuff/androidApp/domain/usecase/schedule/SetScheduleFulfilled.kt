package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository

interface SetScheduleFulfilled {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Long>
}

class SetScheduleFulfilledImpl(
    private val scheduleRepository: ScheduleRepository
) : SetScheduleFulfilled {
    override suspend fun invoke(scheduleId: Long): SimpleResult<Long> {
        return scheduleRepository.setScheduleFulfilled(scheduleId)
    }
}