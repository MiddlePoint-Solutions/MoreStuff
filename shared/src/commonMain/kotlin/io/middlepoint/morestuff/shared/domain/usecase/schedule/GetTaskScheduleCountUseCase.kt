package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository
import io.middlepoint.morestuff.shared.domain.service.TimeManager

interface GetTaskScheduleCountUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Int>
}

class GetTaskScheduleCountUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager,
) : GetTaskScheduleCountUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, Int> {
        val timeRange = timeManager.getTodayTimeRange()
        return scheduleRepository.countTodayTaskSchedules(taskId, timeRange.first, timeRange.second)
    }
}