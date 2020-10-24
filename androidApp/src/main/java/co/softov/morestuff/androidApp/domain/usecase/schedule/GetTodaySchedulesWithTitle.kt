package co.softov.morestuff.androidApp.domain.usecase.schedule

import co.softov.morestuff.androidApp.domain.model.ScheduleWithTitle
import co.softov.morestuff.androidApp.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetTodaySchedulesWithTitle {
    suspend operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetTodaySchedulesWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetTodaySchedulesWithTitle {
    override suspend fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveTodaySchedulesWithTitleFlow()
    }
} 



