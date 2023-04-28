package co.softov.morestuff.android.domain.usecase.priority

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetReviewSchedulesUseCase {
    suspend operator fun invoke(): List<ScheduleWithTitle>
}

class GetReviewSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetReviewSchedulesUseCase {

    override suspend fun invoke(): List<ScheduleWithTitle> =
        scheduleRepository.getActiveSchedulesWithStaleReminders()
}