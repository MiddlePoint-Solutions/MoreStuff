package co.softov.morestuff.android.domain.usecase.schedule

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetSchedulesWithTitleFlow {
    operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetSchedulesWithTitleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesWithTitleFlow {
    override fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveSchedulesWithTitleFlow()
    }
} 



