package co.softov.morestuff.android.domain.usecase.schedule

import kotlinx.coroutines.flow.Flow
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetSchedulesWithTitleList {
    suspend operator fun invoke(): List<ScheduleWithTitle>
}

class GetSchedulesWithTitleListImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesWithTitleList {
    override suspend fun invoke(): List<ScheduleWithTitle> {
        return scheduleRepository.getActiveSchedulesWithTitle()
    }
} 



