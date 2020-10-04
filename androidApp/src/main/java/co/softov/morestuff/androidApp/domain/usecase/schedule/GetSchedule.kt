package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.model.Schedule
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository

interface GetSchedule {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<Schedule>
}

class GetScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedule {

    override suspend fun invoke(scheduleId: Long): SimpleResult<Schedule> {
        return scheduleRepository.getSchedule(scheduleId)
    }
}