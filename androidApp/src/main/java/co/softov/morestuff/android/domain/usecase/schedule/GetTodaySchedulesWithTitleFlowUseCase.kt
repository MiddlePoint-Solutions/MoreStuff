package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.data.service.TimeManager
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetTodaySchedulesWithTitleFlowUseCase {
    operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetTodaySchedulesWithTitleFlowUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager,
) : GetTodaySchedulesWithTitleFlowUseCase {
    override fun invoke(): Flow<List<ScheduleWithTitle>> {
        val time = timeManager.todayTimeStringPair
        return scheduleRepository.getActiveSchedulesWithTitleByTimeFlow(time.first, time.second)
    }
} 



