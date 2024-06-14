package io.middlepoint.morestuff.shared.domain.usecase.schedule

import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.ScheduleDomain
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository

interface GetScheduleUseCase {
    suspend operator fun invoke(scheduleId: Long): Either<Failure, ScheduleDomain>
    suspend operator fun invoke(scheduleIds: List<Long>): Either<Failure, List<ScheduleDomain>>
}

class GetScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetScheduleUseCase {

    override suspend fun invoke(scheduleId: Long): Either<Failure, ScheduleDomain> {
        return scheduleRepository.getSchedule(scheduleId)
    }

    override suspend fun invoke(scheduleIds: List<Long>): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getSchedules(scheduleIds)
    }
}