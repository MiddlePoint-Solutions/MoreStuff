package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleWithTitle
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetSchedulesWithTitleUseCase {
    suspend operator fun invoke(): Either<Failure, List<ScheduleWithTitle>>
}

class GetSchedulesWithTitleListImpl(
    private val scheduleRepository: ScheduleRepository
) : GetSchedulesWithTitleUseCase {
    override suspend fun invoke(): Either<Failure, List<ScheduleWithTitle>> {
        return scheduleRepository.getActiveSchedulesWithTitle()
    }
} 



