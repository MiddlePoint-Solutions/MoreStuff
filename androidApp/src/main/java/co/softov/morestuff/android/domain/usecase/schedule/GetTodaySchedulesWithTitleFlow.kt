package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

interface GetTodaySchedulesWithTitleUseCase {
    operator fun invoke(): Flow<List<ScheduleWithTitle>>
}

class GetTodaySchedulesWithTitleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetTodaySchedulesWithTitleUseCase {
    override fun invoke(): Flow<List<ScheduleWithTitle>> {
        return scheduleRepository.getTodayActiveSchedulesWithTitleFlow()
    }
} 



