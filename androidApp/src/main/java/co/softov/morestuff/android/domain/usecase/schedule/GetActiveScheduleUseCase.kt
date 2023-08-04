package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.model.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveScheduleUseCase {
    suspend operator fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType> = ScheduleType.values().asList()
    ): Either<Failure, List<ScheduleDomain>>
}

class GetActiveScheduleUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveScheduleUseCase {
    override suspend fun invoke(
        taskId: Long,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedulesForTask(taskId, scheduleType)
    }
} 



