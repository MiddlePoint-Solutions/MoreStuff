package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository

interface GetScheduleUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure, Schedule>
    suspend operator fun invoke(scheduleIds: List<Long>): Either<Failure, List<Schedule>>
}

class GetScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleUseCase {

    override suspend fun invoke(scheduleId: Long): Either<Failure, Schedule> {
        return scheduleRepository.getSchedule(scheduleId)
    }

    override suspend fun invoke(scheduleIds: List<Long>): Either<Failure, List<Schedule>> {
        return scheduleRepository.getSchedules(scheduleIds)
    }
}