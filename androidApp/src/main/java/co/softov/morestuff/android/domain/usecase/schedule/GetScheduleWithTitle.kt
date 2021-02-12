package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.model.SimpleResult
import co.softov.morestuff.android.domain.repository.ScheduleRepository

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



