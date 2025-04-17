package io.middlepoint.morestuff.shared.domain.usecase.schedule


import arrow.core.Either
import io.middlepoint.morestuff.shared.domain.model.Failure
import io.middlepoint.morestuff.shared.domain.model.core.Schedule
import io.middlepoint.morestuff.shared.domain.enums.ScheduleType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.repository.ScheduleRepository

interface GetActiveSchedulesUseCase {
    suspend operator fun invoke(
        taskIds: List<Uuid>,
        scheduleType: List<ScheduleType> = ScheduleType.entries
    ): Either<Failure, List<Schedule>>
}

class GetActiveSchedulesUseCaseImpl(
    private val scheduleRepository: ScheduleRepository
) : GetActiveSchedulesUseCase {
    override suspend fun invoke(
        taskIds: List<Uuid>,
        scheduleType: List<ScheduleType>
    ): Either<Failure, List<Schedule>> {
        return scheduleRepository.getActiveSchedulesForTasks(taskIds, scheduleType)
    }
} 



