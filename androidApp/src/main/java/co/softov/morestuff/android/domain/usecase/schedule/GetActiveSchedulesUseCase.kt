package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveSchedulesUseCase {
    suspend operator fun invoke(
        startTime: String? = null,
        endTime: String? = null,
    ): Either<Failure, List<Schedule>>
}

class GetActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedulesUseCase {

    override suspend fun invoke(
        startTime: String?,
        endTime: String?
    ): Either<Failure, List<Schedule>> {
        return scheduleRepository.getActiveSchedules(startTime, endTime)
    }
}