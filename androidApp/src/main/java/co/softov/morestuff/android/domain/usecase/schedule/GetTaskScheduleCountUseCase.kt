package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.usecase.time.GetPriorityTimeUseCase
import timber.log.Timber

interface GetTaskScheduleCountUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Int>
}

class GetTaskScheduleCountUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val getPriorityTimeUseCase: GetPriorityTimeUseCase,
) : GetTaskScheduleCountUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, Int> {
        val timeRange = getPriorityTimeUseCase.getTodayTimeRange()
        Timber.d("### Schedule time range, $timeRange ###")
        return scheduleRepository.countTodayTaskSchedules(taskId, timeRange.first, timeRange.second)
    }
}