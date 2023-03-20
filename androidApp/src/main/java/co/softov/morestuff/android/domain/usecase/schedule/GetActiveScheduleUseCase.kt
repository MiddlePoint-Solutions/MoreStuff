package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.Schedule
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveScheduleUseCase {
    suspend operator fun invoke(taskId: Long): Either<Failure, Schedule>
}

class GetActiveScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleUseCase {
    override suspend fun invoke(taskId: Long): Either<Failure, Schedule> {
        return scheduleRepository.getActiveScheduleForTask(taskId)
    }
} 



