package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveSchedule {
    suspend operator fun invoke(taskId: Long): Either<Failure, Schedule>
}

class GetActiveScheduleImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedule {
    override suspend fun invoke(taskId: Long): Either<Failure, Schedule> {
        return scheduleRepository.getActiveScheduleForTask(taskId)
    }
} 



