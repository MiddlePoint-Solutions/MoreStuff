package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetTomorrowSchedulesWithTitle {
    suspend operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetTomorrowSchedulesWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetTomorrowSchedulesWithTitle {
    override suspend fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveTomorrowSchedulesWithTitleFlow()
    }
} 



