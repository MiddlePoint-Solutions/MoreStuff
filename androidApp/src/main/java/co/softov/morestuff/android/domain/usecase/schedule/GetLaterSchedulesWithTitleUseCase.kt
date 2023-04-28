package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetLaterSchedulesWithTitleUseCase {
    suspend operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetLaterSchedulesWithTitleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetLaterSchedulesWithTitleUseCase {
    override suspend fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getLaterActiveSchedulesWithTitleFlow()
    }
} 



