package io.middlepoint.morestuff.android.domain.usecase.schedule


import arrow.core.Either
import io.middlepoint.morestuff.android.domain.model.Failure
import io.middlepoint.morestuff.android.domain.model.ScheduleDomain
import io.middlepoint.morestuff.android.domain.enums.ScheduleType
import io.middlepoint.morestuff.android.domain.repository.ScheduleRepository

interface GetActiveSchedulesUseCase {
    suspend operator fun invoke(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType> = ScheduleType.entries
    ): Either<Failure, List<ScheduleDomain>>
}

class GetActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedulesUseCase {
    override suspend fun invoke(
        taskIds: List<Long>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<ScheduleDomain>> {
        return scheduleRepository.getActiveSchedulesForTasks(taskIds, scheduleType)
    }
} 



