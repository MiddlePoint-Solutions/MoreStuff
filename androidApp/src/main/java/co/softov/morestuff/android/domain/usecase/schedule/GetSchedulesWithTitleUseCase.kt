package co.softov.morestuff.android.domain.usecase.schedule

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetSchedulesWithTitleUseCase {
    suspend operator fun invoke(): List<ScheduleWithTitle>
}

class GetSchedulesWithTitleListImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesWithTitleUseCase {
    override suspend fun invoke(): List<ScheduleWithTitle> {
        return scheduleRepository.getActiveSchedulesWithTitle()
    }
} 



