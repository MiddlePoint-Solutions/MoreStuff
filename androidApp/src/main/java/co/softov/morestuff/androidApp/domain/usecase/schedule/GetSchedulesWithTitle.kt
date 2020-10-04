package co.softov.morestuff.androidApp.domain.usecase.schedule

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.androidApp.domain.model.SimpleResult
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository

interface GetSchedulesWithTitle {
    suspend operator fun invoke(): SimpleResult<Flow<List<ScheduleWithTitle>>>
}

class GetSchedulesWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesWithTitle {
    override suspend fun invoke(): SimpleResult<Flow<List<ScheduleWithTitle>>> {
        return scheduleRepository.getActiveSchedulesWithTitleFlow()
    }
} 



