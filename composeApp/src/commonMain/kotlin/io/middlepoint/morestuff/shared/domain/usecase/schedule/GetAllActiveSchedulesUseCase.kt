package io.middlepoint.morestuff.shared.domain.usecase.schedule


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository

interface GetAllActiveSchedulesUseCase {
    suspend operator fun invoke(): Either<Failure, List<Schedule>>
}

class GetAllActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetAllActiveSchedulesUseCase {

    override suspend fun invoke(): Either<Failure, List<Schedule>> {
        return scheduleRepository.getActiveSchedules()
    }
}