package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveSchedulesUseCase {
    suspend operator fun invoke(): Either<Failure, List<ScheduleDomain>>
}

class GetActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedulesUseCase {

    override suspend fun invoke(): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedules()
    }
}