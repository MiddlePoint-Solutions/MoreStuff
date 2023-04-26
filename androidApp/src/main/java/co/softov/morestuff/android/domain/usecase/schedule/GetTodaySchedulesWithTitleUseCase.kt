package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetTodaySchedulesWithTitleUseCase {
    suspend operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetTodaySchedulesWithTitleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetTodaySchedulesWithTitleUseCase {
    override suspend fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveTodaySchedulesWithTitleFlow()
    }
} 



