package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.service.TimeManager
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetTodaySchedulesWithTitleUseCase {
    suspend operator fun invoke(): Either<Failure, List<ScheduleWithTitle>>
}

class GetTodaySchedulesWithTitleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository,
    private val timeManager: TimeManager,
) : GetTodaySchedulesWithTitleUseCase {
    override suspend fun invoke(): Either<Failure, List<ScheduleWithTitle>> {
        val time = timeManager.todayTimeStringPair
        return scheduleRepository.getActiveSchedulesWithTitleByTime(time.first, time.second)
    }
} 



