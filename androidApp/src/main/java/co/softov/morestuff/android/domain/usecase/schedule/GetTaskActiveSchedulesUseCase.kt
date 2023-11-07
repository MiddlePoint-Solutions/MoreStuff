package co.softov.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import co.softov.morestuff.android.domain.model.Failure
import co.softov.morestuff.android.domain.model.ScheduleDomain
import co.softov.morestuff.android.domain.enums.ScheduleType
import co.softov.morestuff.android.domain.repository.ScheduleRepository

interface GetTaskActiveSchedulesUseCase {
    suspend operator fun invoke(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType> = ScheduleType.entries
    ): Either<Failure, List<ScheduleDomain>>
}

class GetTaskActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetTaskActiveSchedulesUseCase {
    override suspend fun invoke(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedulesForTasks(taskIds, scheduleType)
    }
} 



