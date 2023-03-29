package co.softov.morestuff.android.domain.usecase.priority

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetSchedulesForPriorityReviewUseCase {
    suspend operator fun invoke(): List<ScheduleWithTitle>
}

class GetSchedulesForPriorityReviewUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesForPriorityReviewUseCase {

    override suspend fun invoke(): List<ScheduleWithTitle> =
        scheduleRepository.getActiveSchedulesWithStaleReminders()
}