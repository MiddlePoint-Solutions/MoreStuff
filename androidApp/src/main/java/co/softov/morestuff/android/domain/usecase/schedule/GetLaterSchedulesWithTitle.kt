package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetLaterSchedulesWithTitle {
    suspend operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetLaterSchedulesWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetLaterSchedulesWithTitle {
    override suspend fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveLaterSchedulesWithTitleFlow()
    }
} 



