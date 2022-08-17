package co.softov.morestuff.android.domain.usecase.schedule

import arrow.core.Either
import co.softov.morestuff.android.domain.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetScheduleUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure, Schedule>
}

class GetScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleUseCase {

    override suspend fun invoke(scheduleId: Long): Either<Failure,Schedule> {
        return scheduleRepository.getSchedule(scheduleId)
    }
}