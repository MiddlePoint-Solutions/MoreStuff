package co.softov.morestuff.android.domain.usecase.priority

import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.usecase.task.GetDefaultPriorityScoreUseCase
import co.softov.morestuff.android.domain.usecase.task.GetPlanPriorityScoreUseCase

interface UpdateScheduledPriorityUseCase {
    suspend operator fun invoke(): List<ScheduleWithTitle>
}

class UpdateScheduledPriorityUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val getPlanPriorityScoreUseCase: GetPlanPriorityScoreUseCase,
) : UpdateScheduledPriorityUseCase {

    override suspend fun invoke(): List<ScheduleWithTitle> =
        scheduleRepository.getActiveSchedulesWithStaleReminders()
}