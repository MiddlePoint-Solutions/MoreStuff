package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository

interface GetScheduleWithTitle {
    suspend operator fun invoke(scheduleId: Long): SimpleResult<ScheduleWithTitle>
}

class GetScheduleWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleWithTitle {
    override suspend fun invoke(scheduleId: Long): SimpleResult<ScheduleWithTitle> {
        return scheduleRepository.getActiveScheduleWithTitle(scheduleId)
    }
} 



