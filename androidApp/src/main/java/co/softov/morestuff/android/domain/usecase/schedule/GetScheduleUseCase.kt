package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetScheduleUseCase {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Schedule>
}

class GetScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleUseCase {

    override suspend fun invoke(scheduleId: Long): SimpleResult<Schedule> {
        return scheduleRepository.getSchedule(scheduleId)
    }
}