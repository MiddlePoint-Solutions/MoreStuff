package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.repository.ScheduleRepository
import co.softov.morestuff.android.domain.service.TimeManager
import timber.log.Timber

interface CountTaskSchedulesUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Int>
}

class CountTaskSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager
) : CountTaskSchedulesUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure,Int> {
        val timeRange = timeManager.getTodayTimeRange()
        Timber.d("### Schedule time range, $timeRange ###")
        return scheduleRepository.countTodayTaskSchedules(taskId, timeRange.first, timeRange.second)
    }
}