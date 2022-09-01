package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveSchedules {
    suspend operator fun invoke(): Either<Failure, List<Schedule>>
}

class GetActiveSchedulesImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedules {

    override suspend fun invoke(): Either<Failure,List<Schedule>> {
        return scheduleRepository.getActiveSchedules()
    }
}