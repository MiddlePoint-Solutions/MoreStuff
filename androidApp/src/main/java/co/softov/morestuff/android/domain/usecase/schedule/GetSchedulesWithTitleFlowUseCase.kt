package co.softov.morestuff.android.domain.usecase.schedule

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetSchedulesWithTitleFlowUseCase {
    operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetSchedulesWithTitleImplUseCase(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesWithTitleFlowUseCase {
    override fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveSchedulesWithTitleFlow()
    }
} 



