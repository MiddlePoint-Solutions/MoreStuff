package co.softov.morestuff.android.domain.usecase.schedule


import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveSchedules {
    suspend operator fun invoke(): SimpleResult<List<Schedule>>
}

class GetActiveSchedulesImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedules {

    override suspend fun invoke(): SimpleResult<List<Schedule>> {
        return scheduleRepository.getActiveSchedules()
    }
}