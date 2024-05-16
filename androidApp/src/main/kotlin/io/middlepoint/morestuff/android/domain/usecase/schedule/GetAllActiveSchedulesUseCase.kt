package io.middlepoint.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.repository.ScheduleRepository

interface GetAllActiveSchedulesUseCase {
    suspend operator fun invoke(): Either<Failure, List<ScheduleDomain>>
}

class GetAllActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetAllActiveSchedulesUseCase {

    override suspend fun invoke(): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedules()
    }
}